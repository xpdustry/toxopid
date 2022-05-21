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
package fr.xpdustry.toxopid.util

import fr.xpdustry.toxopid.ModPlatform
import fr.xpdustry.toxopid.Toxopid
import fr.xpdustry.toxopid.ToxopidExtension
import net.kyori.mammoth.Extensions
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.JavaPlugin
import java.net.URI

/**
 * Adds the jitpack maven repo to the artifacts of Anuken.
 */
fun RepositoryHandler.anukenJitpack() = maven {
    it.name = "anuken-jitpack"
    it.url = URI("https://www.jitpack.io")
    it.mavenContent { c -> c.includeGroupByRegex("^com\\.github\\.Anuken.*") }
}

/**
 * Adds mindustry artifacts as project dependencies :
 * - It adds `arc-core` and `mindustry-core` by default.
 * - If [ModPlatform.HEADLESS] is present in the target platforms,
 *   `arc-backend-headless` and `mindustry-server` are added.
 * - If [ModPlatform.DESKTOP] is present in the target platforms,
 *   `mindustry-desktop` is added.
 */
fun Project.mindustryDependencies() {
    if (!project.plugins.hasPlugin(JavaPlugin::class.java)) {
        throw IllegalArgumentException(
            "You can't add Mindustry dependencies without applying the Java Gradle plugin."
        )
    }
    val extension = Extensions.findOrCreate(
        project.extensions,
        Toxopid.EXTENSION_NAME,
        ToxopidExtension::class.java
    )
    if (extension.compileVersion.isPresent) {
        val version = extension.compileVersion.get()
        project.mindustryDependency("com.github.Anuken.Arc:arc-core:$version")
        project.mindustryDependency("com.github.Anuken.Mindustry:core:$version")
        if (extension.platforms.get().contains(ModPlatform.HEADLESS)) {
            project.mindustryDependency("com.github.Anuken.Arc:backend-headless:$version")
            project.mindustryDependency("com.github.Anuken.Mindustry:server:$version")
        }
        if (extension.platforms.get().contains(ModPlatform.DESKTOP)) {
            project.mindustryDependency("com.github.Anuken.Mindustry:desktop:$version")
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
