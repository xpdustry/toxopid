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
package com.xpdustry.toxopid.plugin

import com.xpdustry.toxopid.extension.toxopid
import com.xpdustry.toxopid.spec.ModPlatform
import com.xpdustry.toxopid.task.MindustryExec
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.named

/**
 * This plugin sets the output of the `jar` task as the mod final jar
 * for every eligible mindustry exec task.
 */
public class ToxopidJavaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.named<MindustryExec>(MindustryExec.DESKTOP_EXEC_TASK_NAME) {
            if (project.toxopid.platforms.get().contains(ModPlatform.DESKTOP)) {
                mods.from(project.tasks.named(JavaPlugin.JAR_TASK_NAME, Jar::class.java))
            }
        }
        project.tasks.named<MindustryExec>(MindustryExec.SERVER_EXEC_TASK_NAME) {
            if (project.toxopid.platforms.get().contains(ModPlatform.SERVER)) {
                mods.from(project.tasks.named(JavaPlugin.JAR_TASK_NAME, Jar::class.java))
            }
        }
    }
}
