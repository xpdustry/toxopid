/*
 * This file is part of Toxopid, a Gradle plugin for Mindustry mods/plugins.
 *
 * MIT License
 *
 * Copyright (c) 2024 Xpdustry
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.xpdustry.toxopid.task

import com.xpdustry.toxopid.extension.HTTP
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.FileSystems
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.io.path.absolutePathString
import kotlin.io.path.copyTo
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.CompileClasspath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.property
import org.w3c.dom.Node

/**
 * Dexes a jar file using d8. It does so by finding the latest Android SDK installed on the system and using the d8 tool
 * from it.
 */
@CacheableTask
public open class DexJar : DefaultTask() {
    /** The source jar file to dex. */
    @get:[InputFile Classpath]
    public val source: RegularFileProperty = project.objects.fileProperty()

    /** The output dexed jar file. */
    @get:OutputFile public val output: RegularFileProperty = project.objects.fileProperty()

    /**
     * The minimum sdk version to target.
     *
     * **Only change if you know what you are doing.**
     */
    @get:Input public val minSdkVersion: Property<Int> = project.objects.property()

    /** The classpath of the [source] jar file. */
    @get:[InputFiles CompileClasspath]
    public val classpath: ConfigurableFileCollection = project.objects.fileCollection()

    /** The platform version to use. If not set, version 35 will be used. */
    @get:Input public val platformVersion: Property<String> = project.objects.property()

    /** The version of r8 to use. If not set, version 8.5.10 will be used. */
    @get:Input public val r8Version: Property<String> = project.objects.property()

    init {
        minSdkVersion.convention(14)
        output.convention { temporaryDir.resolve("output.jar") }
        platformVersion.convention("35")
        r8Version.convention("8.5.35")
    }

    @TaskAction
    public fun dex() {
        val platform = resolveAndroidPlatform()
        val r8lib = resolveR8()

        project.javaexec {
            mainClass = "com.android.tools.r8.D8"
            classpath(r8lib)
            args = buildList {
                add("--lib")
                add(platform.absolutePathString())
                this@DexJar.classpath.forEach { file ->
                    add("--classpath")
                    add(file.absolutePath)
                }
                add("--min-api")
                add(minSdkVersion.get().toString())
                add("--output")
                add(output.get().asFile.absolutePath)
                add(source.get().asFile.absolutePath)
            }
        }
    }

    private fun resolveAndroidPlatform(): Path {
        logger.debug("Resolving Android SDK")

        val target =
            project.gradle.gradleUserHomeDir.resolve(
                "caches/toxopid/android-platforms/android-${platformVersion.get()}.jar"
            )
        if (target.exists()) {
            logger.debug("Using cached Android SDK at {}", target)
            return target.toPath()
        }

        target.parentFile.mkdirs()

        val repository =
            HTTP.send(
                HttpRequest.newBuilder(URI("https://dl.google.com/android/repository/repository2-3.xml")).GET().build(),
                BodyHandlers.ofInputStream(),
            )

        if (repository.statusCode() != 200) {
            error("Failed to download google android repository listing: (code=${repository.statusCode()})")
        }

        val document =
            repository.body().use {
                val factory = DocumentBuilderFactory.newInstance()
                factory.isIgnoringComments = true
                factory.newDocumentBuilder().parse(it)
            }

        val packages = document.childNodes.item(0).getNodes("remotePackage")
        logger.debug(
            "Found ${packages.size} android packages: {}",
            packages.joinToString { it.attributes.getNamedItem("path").nodeValue },
        )
        val match =
            packages.find { node ->
                val path = node.attributes.getNamedItem("path").nodeValue
                path.removePrefix("platforms;android-") == platformVersion.get()
            } ?: error("No Android SDK found in google repository for version ${platformVersion.get()}")

        val url = match["archives"]!!["archive"]!!["complete"]!!["url"]!!.textContent
        logger.debug("Downloading Android SDK from $url")

        val platform =
            HTTP.send(
                HttpRequest.newBuilder(URI("https://dl.google.com/android/repository/$url")).GET().build(),
                BodyHandlers.ofFile(temporaryDir.resolve("android-${platformVersion.get()}.zip").toPath()),
            )

        if (platform.statusCode() != 200) {
            error("Failed to download android platform: (code=${platform.statusCode()})")
        }

        FileSystems.newFileSystem(
                temporaryDir.resolve("android-${platformVersion.get()}.zip").toPath(),
                null as ClassLoader?,
            )
            .use { it.getPath("android-${platformVersion.get()}", "android.jar").copyTo(target.toPath()) }

        logger.debug("Downloaded Android SDK version ${platformVersion.get()}")
        return target.toPath()
    }

    private fun Node.getNodes(name: String): List<Node> {
        val children = childNodes
        val list = mutableListOf<Node>()
        repeat(children.length) {
            val node = children.item(it)
            if (node.nodeName == name) list += node
        }
        return list
    }

    private operator fun Node.get(name: String): Node? {
        val children = childNodes
        repeat(children.length) {
            val node = children.item(it)
            if (node.nodeName == name) return node
        }
        return null
    }

    private fun resolveR8(): Path {
        logger.debug("Resolving R8")

        val r8lib = project.gradle.gradleUserHomeDir.resolve("caches/toxopid/r8/r8lib-${r8Version.get()}.jar")
        if (r8lib.exists()) {
            logger.debug("Using cached r8lib at {}", r8lib)
            return r8lib.toPath()
        }

        r8lib.parentFile.mkdirs()

        val response =
            HTTP.send(
                HttpRequest.newBuilder(
                        URI("https://storage.googleapis.com/r8-releases/raw/${r8Version.get()}/r8lib.jar")
                    )
                    .GET()
                    .build(),
                BodyHandlers.ofFile(r8lib.toPath()),
            )

        if (response.statusCode() != 200) {
            error("Failed to download r8lib: (code=${response.statusCode()}, version=${r8Version.get()})")
        }

        logger.debug("Downloaded r8 version ${r8Version.get()}")
        return r8lib.toPath()
    }

    public companion object {
        public const val DEX_TASK_NAME: String = "dexJar"
    }
}
