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

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import java.net.URI

open class ToxopidExtension(private val project: Project) {
    /**
     * Mindustry compile version for [ToxopidExtension.mindustryDependencies].
     * The default version is v126.2.
     */
    val compileVersion: Property<String> = project.objects.property(String::class.java)
    /**
     * Mindustry runtime version for [fr.xpdustry.toxopid.task.MindustryExec] tasks.
     * If not set, fallbacks to [ToxopidExtension.compileVersion].
     */
    val runtimeVersion: Property<String> = project.objects.property(String::class.java)
    /**
     * Target platforms for the mod/plugin. It can add dependencies, tasks...
     */
    val platforms: SetProperty<ModPlatform> = project.objects.setProperty(ModPlatform::class.java)

    init {
        compileVersion.convention("v126.2")
        runtimeVersion.convention(project.provider { compileVersion.get() })
        platforms.convention(setOf(ModPlatform.DESKTOP))
    }

    /**
     * Adds the jitpack maven repo to the artifacts of Anuken.
     */
    fun jitpackAnuken() {
        project.repositories.maven {
            it.name = "jitpack-anuken"
            it.url = URI("https://www.jitpack.io")
            it.mavenContent { c -> c.includeGroupByRegex("^com\\.github\\.Anuken.*") }
        }
    }

    /**
     * Adds mindustry artifacts as project dependencies :
     * - It adds `arc-core` and `mindustry-core` by default.
     * - If [ModPlatform.HEADLESS] is present in the target platforms,
     *   `arc-backend-headless` and `mindustry-server` are added.
     * - If [ModPlatform.DESKTOP] is present in the target platforms,
     *   `mindustry-desktop` is added.
     */
    fun mindustryDependencies() {
        if (!project.plugins.hasPlugin(JavaPlugin::class.java)) {
            throw IllegalArgumentException("You can't add Mindustry dependencies without applying the Java Gradle plugin.")
        }
        if (compileVersion.isPresent) {
            val compileVersion = compileVersion.get()
            project.mindustryDependency("com.github.Anuken.Arc:arc-core:$compileVersion")
            project.mindustryDependency("com.github.Anuken.Mindustry:core:$compileVersion")
            if (platforms.get().contains(ModPlatform.HEADLESS)) {
                project.mindustryDependency("com.github.Anuken.Arc:backend-headless:$compileVersion")
                project.mindustryDependency("com.github.Anuken.Mindustry:server:$compileVersion")
            }
            if (platforms.get().contains(ModPlatform.DESKTOP)) {
                project.mindustryDependency("com.github.Anuken.Mindustry:desktop:$compileVersion")
            }
        }
    }

    private fun Project.dependency(configuration: String, dependency: String) {
        configurations.getByName(configuration).dependencies.add(dependencies.create(dependency))
    }

    private fun Project.mindustryDependency(dependency: String) {
        dependency("compileOnly", dependency)
        dependency("testImplementation", dependency)
    }
}
