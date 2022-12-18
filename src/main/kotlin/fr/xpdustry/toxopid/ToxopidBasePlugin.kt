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
package fr.xpdustry.toxopid

import fr.xpdustry.toxopid.task.GithubArtifactDownload
import fr.xpdustry.toxopid.task.MindustryExec
import net.kyori.mammoth.Extensions
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Base plugin that sets up the standard toxopid tasks for mod/plugin testing.
 */
class ToxopidBasePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = Extensions.findOrCreate(
            project.extensions,
            Toxopid.EXTENSION_NAME,
            ToxopidExtension::class.java
        )

        project.dependencies.extensions.add(Toxopid.EXTENSION_NAME, extension)

        val downloadMindustryClient = project.tasks.register(
            "downloadMindustryClient",
            GithubArtifactDownload::class.java
        ) {
            it.group = Toxopid.TASK_GROUP_NAME
            it.user.set("Anuken")
            it.repo.set("Mindustry")
            it.name.set("Mindustry.jar")
            it.version.set(extension.runtimeVersion)
        }

        val downloadMindustryServer = project.tasks.register(
            "downloadMindustryServer",
            GithubArtifactDownload::class.java
        ) {
            it.group = Toxopid.TASK_GROUP_NAME
            it.user.set("Anuken")
            it.repo.set("Mindustry")
            it.name.set("server-release.jar")
            it.version.set(extension.runtimeVersion)
        }

        project.tasks.register("runMindustryClient", MindustryExec::class.java) {
            it.group = Toxopid.TASK_GROUP_NAME
            it.classpath(downloadMindustryClient)
            it.mainClass.convention("mindustry.desktop.DesktopLauncher")
            it.modsPath.convention("./mods")
            it.standardInput = System.`in`
        }

        project.tasks.register("runMindustryServer", MindustryExec::class.java) {
            it.group = Toxopid.TASK_GROUP_NAME
            it.classpath(downloadMindustryServer)
            it.mainClass.convention("mindustry.server.ServerLauncher")
            it.modsPath.convention("./config/mods")
            it.standardInput = System.`in`
        }
    }
}
