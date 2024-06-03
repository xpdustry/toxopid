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

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import org.hjson.JsonObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Downloads a release asset from a GitHub repository.
 */
@CacheableTask
public open class GithubAssetDownload : DefaultTask() {
    /**
     * The repository owner.
     */
    @get:Input
    public val owner: Property<String> = project.objects.property()

    /**
     * The repository name.
     */
    @get:Input
    public val repo: Property<String> = project.objects.property()

    /**
     * The name of the asset.
     */
    @get:Input
    public val asset: Property<String> = project.objects.property()

    /**
     * The release version.
     */
    @get:Input
    public val version: Property<String> = project.objects.property()

    /**
     * The GitHub access token to use for downloading the asset.
     */
    @get:[Input Optional]
    public val token: Property<String> = project.objects.property()

    /**
     * The output file.
     *
     * *Default location is `{gradle-user-home}/caches/toxopid/github-assets/{owner}/{repo}/{version}/{asset}`.*
     */
    @get:OutputFile
    public val output: RegularFileProperty = project.objects.fileProperty()

    init {
        output.convention {
            project.gradle.gradleUserHomeDir.resolve(
                "caches/toxopid/github-assets/${owner.get()}/${repo.get()}/${version.get()}/${asset.get()}",
            )
        }
    }

    @TaskAction
    public fun download() {
        val release =
            HTTP.send(
                HttpRequest.newBuilder(
                    URI("https://api.github.com/repos/${owner.get()}/${repo.get()}/releases/tags/${version.get()}"),
                )
                    .header("Accept", "application/vnd.github+json")
                    .applyAuthorization()
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString(),
            )

        val json = JsonObject.readJSON(release.body())
        if (release.statusCode() != 200) {
            error("Failed to get release: (code=${release.statusCode()}, message=${json.asObject()["message"]})")
        }

        val asset =
            json.asObject()["assets"].asArray()
                .map { it.asObject() }
                .firstOrNull { it["name"].asString() == asset.get() }
                ?: error("Failed to find asset named $asset")

        val download =
            HTTP.send(
                HttpRequest.newBuilder(URI(asset["url"].asString()))
                    .header("Accept", "application/octet-stream")
                    .applyAuthorization()
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofFile(output.asFile.get().toPath()),
            )

        if (download.statusCode() != 200) {
            error("Failed to download asset: (code=${download.statusCode()})")
        }
    }

    private fun HttpRequest.Builder.applyAuthorization(): HttpRequest.Builder =
        apply {
            if (token.isPresent) {
                header("Authorization", "Bearer ${token.get()}")
            }
        }

    public companion object {
        public const val MINDUSTRY_DESKTOP_DOWNLOAD_TASK_NAME: String = "downloadMindustryDesktop"
        public const val MINDUSTRY_SERVER_DOWNLOAD_TASK_NAME: String = "downloadMindustryServer"
        private val HTTP = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()
    }
}
