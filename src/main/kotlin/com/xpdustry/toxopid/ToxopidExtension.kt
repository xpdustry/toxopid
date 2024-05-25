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
package com.xpdustry.toxopid

import com.xpdustry.toxopid.spec.ModPlatform
import org.gradle.api.artifacts.Dependency
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty

public interface ToxopidExtension {
    /**
     * Mindustry compile version for dependency resolution.
     * If not set, the default version is [Toxopid.DEFAULT_MINDUSTRY_VERSION].
     */
    public val compileVersion: Property<String>

    /**
     * Mindustry runtime version for [com.xpdustry.toxopid.task.MindustryExec] tasks.
     * If not set, fallbacks to [ToxopidExtension.compileVersion].
     */
    public val runtimeVersion: Property<String>

    /**
     * Target platforms for the mod/plugin. It can add dependencies, tasks...
     * If not set, the default platform is [ModPlatform.DESKTOP].
     */
    public val platforms: SetProperty<ModPlatform>

    /**
     * Mindustry dependencies for the mod/plugin.
     */
    public val dependencies: Dependencies

    /**
     * Typesafe mindustry dependencies provider.
     */
    public interface Dependencies {
        public val mindustryCore: Provider<Dependency>
        public val arcCore: Provider<Dependency>
        public val mindustryHeadless: Provider<Dependency>
        public val arcHeadless: Provider<Dependency>
    }
}
