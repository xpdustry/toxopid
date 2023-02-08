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

import org.gradle.api.Plugin
import org.gradle.api.Project

class ToxopidGroovyPlugin implements Plugin<Project> {

	@Override
	void apply(final Project project) {
		// I hate groovy
		final def extensionClass = Class.forName("fr.xpdustry.toxopid.dsl.DependencyExtensions")
		project.repositories.metaClass.anukenJitpack = {
			extensionClass.anukenJitpack(project.repositories)
		}
		project.dependencies.metaClass.mindustryDependencies = {
			extensionClass.mindustryDependencies(project.dependencies)
		}
		project.dependencies.metaClass.mindustryCoreDependencies = {
			extensionClass.mindustryCoreDependencies(project.dependencies)
		}
		project.dependencies.metaClass.mindustryHeadlessDependencies = {
			extensionClass.mindustryHeadlessDependencies(project.dependencies)
		}
	}
}
