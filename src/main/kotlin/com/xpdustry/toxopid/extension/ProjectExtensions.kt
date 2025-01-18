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
package com.xpdustry.toxopid.extension

import com.xpdustry.toxopid.Toxopid
import com.xpdustry.toxopid.ToxopidExtension
import com.xpdustry.toxopid.ToxopidExtensionImpl
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.named

internal inline val Project.toxopid: ToxopidExtension
    get() =
        extensions.findByName(Toxopid.EXTENSION_NAME) as ToxopidExtension?
            ?: extensions.create(ToxopidExtension::class, Toxopid.EXTENSION_NAME, ToxopidExtensionImpl::class)

internal fun Project.getJarTask(): TaskProvider<out Jar> =
    if (
        plugins.hasPlugin("com.github.johnrengelman.shadow") ||
            plugins.hasPlugin("io.github.goooler.shadow") ||
            plugins.hasPlugin("com.gradleup.shadow")
    ) {
        tasks.named<Jar>("shadowJar")
    } else if (plugins.hasPlugin(JavaPlugin::class)) {
        tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)
    } else {
        error("No valid jar task found")
    }
