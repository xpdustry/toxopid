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

import fr.xpdustry.toxopid.spec.ModPlatform
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

open class ToxopidExtension(project: Project) {
    /**
     * Mindustry compile version for dependency resolution.
     * The default version is v143.
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
        compileVersion.convention("v${Toxopid.DEFAULT_MINDUSTRY_VERSION}")
        runtimeVersion.convention(project.provider(compileVersion::get))
        platforms.convention(setOf(ModPlatform.DESKTOP))
    }
}
