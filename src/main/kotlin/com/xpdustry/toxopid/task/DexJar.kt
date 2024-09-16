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

import com.xpdustry.toxopid.Toxopid
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
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.property
import java.io.File
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.listDirectoryEntries

/**
 * Dexes a jar file using d8. It does so by finding the latest Android SDK installed on the system and using the d8 tool from it.
 */
@CacheableTask
public open class DexJar : DefaultTask() {
    /**
     * The source jar file to dex.
     */
    @get:[InputFile Classpath]
    public val source: RegularFileProperty = project.objects.fileProperty()

    /**
     * The output dexed jar file.
     */
    @get:OutputFile
    public val output: RegularFileProperty = project.objects.fileProperty()

    /**
     * The minimum sdk version to target.
     *
     * **Only change if you know what you are doing.**
     */
    @get:Input
    public val minSdkVersion: Property<Int> = project.objects.property()

    /**
     * The classpath of the [source] jar file.
     */
    @get:[InputFiles CompileClasspath]
    public val classpath: ConfigurableFileCollection = project.objects.fileCollection()

    /**
     * The version of d8 to use. If not set, the d8 tool from the selected local Android SDK will be used.
     */
    @get:[Input Optional]
    public val d8Version: Property<String> = project.objects.property()

    init {
        minSdkVersion.convention(14)
        output.convention { temporaryDir.resolve("output.jar") }
    }

    @TaskAction
    public fun dex() {
        val sdk =
            if (project.hasProperty("sdk.dir")) {
                project.property("sdk.dir") as String
            } else {
                System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
            }

        if (sdk == null || sdk.isEmpty() || !File(sdk).exists()) {
            error(
                """
                No valid Android SDK found. Ensure that ANDROID_HOME is set to your Android SDK directory.
                Note: if the gradle daemon has been started before ANDROID_HOME env variable was defined, it won't be able to read this variable.
                In this case you have to run "./gradlew --stop" and try again
                """.trimIndent(),
            )
        }

        val (platform, version) =
            Path(sdk).resolve("platforms").listDirectoryEntries()
                .map { it to it.fileName.toString().removePrefix("android-").toInt() }
                .maxByOrNull { it.second }
                ?: error("No Android SDK found")

        logger.info("Using Android SDK at {}", version)

        val arguments =
            buildList<String> {
                add("--lib")
                add(platform.resolve("android.jar").absolutePathString())
                classpath.forEach {
                    add("--classpath")
                    add(it.toPath().absolutePathString())
                }
                add("--min-api")
                add(minSdkVersion.get().toString())
                add("--output")
                add(output.get().asFile.absolutePath)
                add(source.get().asFile.absolutePath)
            }

        if (d8Version.isPresent) {
            logger.info("Dexing jar using explicit d8 version: ${d8Version.get()}")

            val r8lib = project.gradle.gradleUserHomeDir.resolve("caches/toxopid/r8/r8lib-${d8Version.get()}.jar")
            r8lib.parentFile.mkdirs()

            if (!r8lib.exists()) {
                val response =
                    Toxopid.HTTP.send(
                        HttpRequest.newBuilder(URI("https://storage.googleapis.com/r8-releases/raw/${d8Version.get()}/r8lib.jar"))
                            .GET()
                            .build(),
                        BodyHandlers.ofFile(r8lib.toPath()),
                    )

                if (response.statusCode() != 200) {
                    error("Failed to download r8lib: (code=${response.statusCode()}, version=${d8Version.get()})")
                }

                logger.info("Downloaded r8 version ${d8Version.get()}")
            }

            project.javaexec {
                mainClass = "com.android.tools.r8.D8"
                classpath(r8lib)
                args = arguments
            }
        } else {
            val d8 =
                Path(sdk)
                    .resolve("build-tools")
                    .listDirectoryEntries()
                    .filter { it.fileName.toString().startsWith(version.toString()) }
                    .maxOrNull()
                    ?.resolve(if (System.getProperty("os.name").lowercase().contains("windows")) "d8.bat" else "d8")
                    ?.absolutePathString()
                    ?: error("No d8 found in Android SDK for version $version")

            logger.info("Dexing jar using user d8: $d8")

            project.exec {
                executable = d8
                args = arguments
            }
        }
    }

    public companion object {
        public const val DEX_TASK_NAME: String = "dexJar"
    }
}
