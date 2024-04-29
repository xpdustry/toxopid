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

import com.xpdustry.toxopid.spec.ModPlatform
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import javax.inject.Inject

internal open class ToxopidExtensionImpl
    @Inject
    constructor(objects: ObjectFactory, dependencies: DependencyHandler, providers: ProviderFactory) : ToxopidExtension {
        final override val compileVersion = objects.property<String>()
        final override val runtimeVersion = objects.property<String>()
        final override val platforms = objects.setProperty<ModPlatform>()
        final override val dependencies =
            DependenciesImpl(
                compileVersion.map { dependencies.create("com.github.Anuken.Mindustry", "core", it) },
                compileVersion.map { dependencies.create("com.github.Anuken.Arc", "arc-core", it) },
                compileVersion.map { dependencies.create("com.github.Anuken.Mindustry", "server", it) },
                compileVersion.map { dependencies.create("com.github.Anuken.Arc", "backend-headless", it) },
            )

        init {
            compileVersion.convention("v${Toxopid.DEFAULT_MINDUSTRY_VERSION}")
            runtimeVersion.convention(providers.provider(compileVersion::get))
            platforms.convention(setOf(ModPlatform.DESKTOP))
        }

        data class DependenciesImpl(
            override val mindustryCore: Provider<Dependency>,
            override val arcCore: Provider<Dependency>,
            override val mindustryHeadless: Provider<Dependency>,
            override val arcHeadless: Provider<Dependency>,
        ) : ToxopidExtension.Dependencies
    }
