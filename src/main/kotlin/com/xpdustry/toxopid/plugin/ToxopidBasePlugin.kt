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
package com.xpdustry.toxopid.plugin

import com.xpdustry.toxopid.Toxopid
import com.xpdustry.toxopid.extension.configureDesktop
import com.xpdustry.toxopid.extension.configureServer
import com.xpdustry.toxopid.extension.toxopid
import com.xpdustry.toxopid.task.GithubAssetDownload
import com.xpdustry.toxopid.task.MindustryExec
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

/**
 * Base plugin that sets up the standard toxopid tasks for mod/plugin testing.
 */
public class ToxopidBasePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.toxopid // Ensures the extension is created

        project.tasks.register<GithubAssetDownload>(GithubAssetDownload.MINDUSTRY_DESKTOP_DOWNLOAD_TASK_NAME) {
            group = Toxopid.TASK_GROUP_NAME
            owner.set("Anuken")
            repo.set("Mindustry")
            asset.set("Mindustry.jar")
            version.set(project.toxopid.runtimeVersion)
        }

        project.tasks.register<GithubAssetDownload>(GithubAssetDownload.MINDUSTRY_SERVER_DOWNLOAD_TASK_NAME) {
            group = Toxopid.TASK_GROUP_NAME
            owner.set("Anuken")
            repo.set("Mindustry")
            asset.set("server-release.jar")
            version.set(project.toxopid.runtimeVersion)
        }

        project.tasks.register<MindustryExec>(MindustryExec.DESKTOP_EXEC_TASK_NAME) {
            group = Toxopid.TASK_GROUP_NAME
            configureDesktop()
        }

        project.tasks.register<MindustryExec>(MindustryExec.SERVER_EXEC_TASK_NAME) {
            group = Toxopid.TASK_GROUP_NAME
            configureServer()
        }

        project.afterEvaluate {
            project.configurations.all {
                resolutionStrategy.eachDependency {
                    if (requested.group == "com.github.Anuken.Arc") {
                        useVersion(project.toxopid.compileVersion.get())
                    }
                }
            }
        }
    }
}
