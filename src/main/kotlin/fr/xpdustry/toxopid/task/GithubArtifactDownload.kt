/*
 * This file is part of Toxopid, a Gradle plugin for Mindustry mods/plugins.
 *
 * MIT License
 *
 * Copyright (c) 2022 Xpdustry
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
package fr.xpdustry.toxopid.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.net.URL

/**
 * Downloads a release artifact from a GitHub repository.
 */
@CacheableTask
public open class GithubArtifactDownload : DefaultTask() {

    /**
     * The repository user.
     */
    @get:Input
    public val user: Property<String> = project.objects.property(String::class.java)

    /**
     * The repository name.
     */
    @get:Input
    public val repo: Property<String> = project.objects.property(String::class.java)

    /**
     * The name of the artifact.
     */
    @get:Input
    public val name: Property<String> = project.objects.property(String::class.java)

    /**
     * The release version.
     */
    @get:Input
    public val version: Property<String> = project.objects.property(String::class.java)

    /**
     * The output file.
     *
     * *Default location is `{gradle-user-home}/caches/toxopid/github-artifacts/{user}/{repo}/{version}/{name}`.*
     */
    @get:OutputFile
    public val output: RegularFileProperty = project.objects.fileProperty()

    init {
        output.convention { project.gradle.gradleUserHomeDir.resolve("caches/toxopid/github-artifacts/${user.get()}/${repo.get()}/${version.get()}/${name.get()}") }
    }

    @TaskAction
    public fun download() {
        val url =
            URL("https://github.com/${user.get()}/${repo.get()}/releases/download/${version.get()}/${name.get()}")
        output.asFile.get().outputStream().use { o -> url.openStream().use { i -> i.copyTo(o) } }
    }
}
