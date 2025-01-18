/*
 * This file is part of Toxopid, a Gradle plugin for Mindustry mods/plugins.
 *
 * MIT License
 *
 * Copyright (c) 2025 Xpdustry
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

package com.xpdustry.toxopid.extension

import java.net.URI
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

/**
 * Adds the [xpdustry repository](https://maven.xpdustry.com/#/mindustry) restricted to Anuke artifacts.
 *
 * Provides mindustry, arc and [anuke rhino fork](https://github.com/Anuken/rhino).
 */
public fun RepositoryHandler.anukeXpdustry(): MavenArtifactRepository =
    createAnukeRepository("xpdustry", "https://maven.xpdustry.com/mindustry")

/**
 * Add the [zelaux repository](https://github.com/Zelaux/MindustryRepo) restricted to Anuke artifacts.
 *
 * Provides mindustry and arc.
 */
public fun RepositoryHandler.anukeZelaux(): MavenArtifactRepository =
    createAnukeRepository("zelaux", "https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository")

/**
 * Adds the [jitpack repository](https://www.jitpack.io) restricted to Anuke artifacts.
 *
 * Provides any published anuke artifact, but fails sometimes. Use as a fallback.
 */
public fun RepositoryHandler.anukeJitpack(): MavenArtifactRepository =
    createAnukeRepository("jitpack", "https://www.jitpack.io")

private fun RepositoryHandler.createAnukeRepository(name: String, uri: String) = maven {
    this.name = "anuke-$name"
    url = URI(uri)
    mavenContent { includeGroupByRegex("^com\\.github\\.Anuken(\\.[\\w-]+)*$") }
    metadataSources {
        gradleMetadata()
        mavenPom()
        artifact()
    }
}
