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

import com.xpdustry.toxopid.Toxopid
import com.xpdustry.toxopid.extension.getJarTask
import com.xpdustry.toxopid.extension.toxopid
import com.xpdustry.toxopid.spec.ModPlatform
import com.xpdustry.toxopid.task.DexJar
import com.xpdustry.toxopid.task.MindustryExec
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

/**
 * This plugin is responsible for handling the bundling of the mod jar:
 *
 * - If only the [java plugin](https://docs.gradle.org/current/userguide/java_plugin.html) is present, the `jar` task is used.
 * - If the [shadow plugin](https://github.com/johnrengelman/shadow) is present alongside the java plugin, the `shadowJar` task is used.
 *
 * For each configured target platform [MindustryExec] tasks, the jar is added to the mods list.
 *
 * Finally, if android is in the target platforms, the jar is dexed and merged in the `mergeJar` task.
 * If not, the `mergeJar` task is equivalent to the jar task.
 */
public class ToxopidJavaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withType<JavaPlugin> {
            val jar = project.getJarTask()

            project.tasks.named<MindustryExec>(MindustryExec.DESKTOP_EXEC_TASK_NAME) {
                if (project.toxopid.platforms.get().contains(ModPlatform.DESKTOP)) {
                    mods.from(jar)
                }
            }

            project.tasks.named<MindustryExec>(MindustryExec.SERVER_EXEC_TASK_NAME) {
                if (project.toxopid.platforms.get().contains(ModPlatform.SERVER)) {
                    mods.from(jar)
                }
            }

            val dexJar =
                project.tasks.register<DexJar>(DexJar.DEX_TASK_NAME) {
                    group = Toxopid.TASK_GROUP_NAME
                    source = jar.flatMap { it.archiveFile }
                    classpath.from(project.configurations.named(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME))
                    classpath.from(project.configurations.named(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME))
                    onlyIf { ModPlatform.ANDROID in project.toxopid.platforms.get() }
                }

            project.tasks.register<Jar>(MERGE_JAR_TASK_NAME) {
                group = Toxopid.TASK_GROUP_NAME
                from(project.zipTree(dexJar.flatMap { it.output }))
                from(project.zipTree(jar.flatMap { it.archiveFile }))
                archiveClassifier.convention("merged")
            }
        }
    }

    private companion object {
        private const val MERGE_JAR_TASK_NAME = "mergeJar"
    }
}
