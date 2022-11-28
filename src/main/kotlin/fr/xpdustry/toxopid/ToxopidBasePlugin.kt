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

import fr.xpdustry.toxopid.task.GitHubArtifact
import fr.xpdustry.toxopid.task.GitHubDownload
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
            GitHubDownload::class.java
        ) {
            it.group = Toxopid.TASK_GROUP_NAME
            it.artifacts.convention(
                project.provider {
                    listOf(
                        GitHubArtifact.release(
                            "Anuken",
                            "Mindustry",
                            extension.runtimeVersion.get(),
                            "Mindustry.jar"
                        )
                    )
                }
            )
        }

        val downloadMindustryServer = project.tasks.register(
            "downloadMindustryServer",
            GitHubDownload::class.java
        ) {
            it.group = Toxopid.TASK_GROUP_NAME
            it.artifacts.convention(
                project.provider {
                    listOf(
                        GitHubArtifact.release(
                            "Anuken",
                            "Mindustry",
                            extension.runtimeVersion.get(),
                            "server-release.jar"
                        )
                    )
                }
            )
        }

        project.tasks.register("runMindustryClient", MindustryExec::class.java) {
            it.group = Toxopid.TASK_GROUP_NAME
            it.classpath.setFrom(downloadMindustryClient)
            it.mainClass.convention("mindustry.desktop.DesktopLauncher")
            it.modsPath.convention("./mods")
        }

        project.tasks.register("runMindustryServer", MindustryExec::class.java) {
            it.group = Toxopid.TASK_GROUP_NAME
            it.classpath.setFrom(downloadMindustryServer)
            it.mainClass.convention("mindustry.server.ServerLauncher")
            it.modsPath.convention("./config/mods")
        }
    }
}
