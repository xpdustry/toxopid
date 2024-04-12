/*
 * This file is part of Toxopid, a Gradle plugin for Mindustry mods/plugins.
 *
 * MIT License
 *
 * Copyright (c) 2023 Xpdustry
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
package com.xpdustry.toxopid

import com.xpdustry.toxopid.task.GithubArtifactDownload
import com.xpdustry.toxopid.task.MindustryExec
import net.kyori.mammoth.Extensions
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Base plugin that sets up the standard toxopid tasks for mod/plugin testing.
 */
public class ToxopidBasePlugin : Plugin<Project> {
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
            group = com.xpdustry.toxopid.Toxopid.TASK_GROUP_NAME
            user.set("Anuken")
            repo.set("Mindustry")
            name.set("Mindustry.jar")
            version.set(extension.runtimeVersion)
        }

        val downloadMindustryServer = project.tasks.register(
            "downloadMindustryServer",
            GithubArtifactDownload::class.java
        ) {
            group = com.xpdustry.toxopid.Toxopid.TASK_GROUP_NAME
            user.set("Anuken")
            repo.set("Mindustry")
            name.set("server-release.jar")
            version.set(extension.runtimeVersion)
        }

        project.tasks.register("runMindustryClient", MindustryExec::class.java) {
            group = com.xpdustry.toxopid.Toxopid.TASK_GROUP_NAME
            classpath(downloadMindustryClient)
            mainClass.convention("mindustry.desktop.DesktopLauncher")
            modsPath.convention("./mods")
            standardInput = System.`in`
        }

        project.tasks.register("runMindustryServer", MindustryExec::class.java) {
            group = com.xpdustry.toxopid.Toxopid.TASK_GROUP_NAME
            classpath(downloadMindustryServer)
            mainClass.convention("mindustry.server.ServerLauncher")
            modsPath.convention("./config/mods")
            standardInput = System.`in`
        }

        project.afterEvaluate {
            project.configurations.all {
                resolutionStrategy.eachDependency {
                    if (requested.group == "com.github.Anuken.Arc") {
                        useVersion(extension.compileVersion.get())
                    }
                }
            }
        }
    }
}
