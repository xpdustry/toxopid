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
@file:JvmName("DependencyExtensions")

package fr.xpdustry.toxopid.dsl

import fr.xpdustry.toxopid.Toxopid
import fr.xpdustry.toxopid.ToxopidExtension
import fr.xpdustry.toxopid.spec.ModPlatform
import net.kyori.mammoth.Extensions
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.ExtensionContainer
import java.net.URI

/**
 * Adds the jitpack repository restricted to Anuke artifacts.
 */
fun RepositoryHandler.anukenJitpack() = maven { repository ->
    repository.name = "anuken-jitpack"
    repository.url = URI("https://www.jitpack.io")
    repository.mavenContent { content ->
        content.includeGroupByRegex("^com\\.github\\.Anuken(\\.[\\w-]+)*$")
    }
    repository.metadataSources { metadata ->
        metadata.gradleMetadata()
        metadata.mavenPom()
        metadata.artifact()
    }
}

/**
 * Adds mindustry artifacts as project dependencies :
 * - It adds `arc-core` and `mindustry-core` by default.
 * - If [ModPlatform.HEADLESS] is present in the target platforms,
 *   `arc-backend-headless` and `mindustry-server` are added.
 */
fun DependencyHandler.mindustryDependencies() {
    mindustryCoreDependencies()
    if (extensions.toxopid.platforms.get().contains(ModPlatform.HEADLESS)) {
        mindustryHeadlessDependencies()
    }
}

fun DependencyHandler.mindustryCoreDependencies() {
    val version = extensions.toxopid.compileVersion.get()
    mindustryDependency("com.github.Anuken.Arc:arc-core:$version")
    mindustryDependency("com.github.Anuken.Mindustry:core:$version")
}

fun DependencyHandler.mindustryHeadlessDependencies() {
    val version = extensions.toxopid.compileVersion.get()
    mindustryDependency("com.github.Anuken.Arc:backend-headless:$version")
    mindustryDependency("com.github.Anuken.Mindustry:server:$version")
}

private fun DependencyHandler.mindustryDependency(dependency: String) {
    add("compileOnly", dependency)
    add("testImplementation", dependency)
}

private val ExtensionContainer.toxopid: ToxopidExtension
    get() = Extensions.findOrCreate(this, Toxopid.EXTENSION_NAME, ToxopidExtension::class.java)
