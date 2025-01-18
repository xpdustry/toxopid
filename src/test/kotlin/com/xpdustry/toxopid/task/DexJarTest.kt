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
package com.xpdustry.toxopid.task

import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.test.assertEquals
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class DexJarTest {
    @TempDir lateinit var directory: Path

    @Test
    fun `test dex and merge`() {
        directory
            .resolve("build.gradle.kts")
            .writeText(
                """
                import com.xpdustry.toxopid.spec.ModPlatform
    
                plugins {
                    java
                    id("com.xpdustry.toxopid")
                }
                
                toxopid {
                    platforms = setOf(ModPlatform.DESKTOP, ModPlatform.ANDROID, ModPlatform.SERVER)
                }
                """
                    .trimIndent()
            )

        val runner =
            GradleRunner.create()
                .withProjectDir(directory.toFile())
                .withArguments("mergeJar")
                .withPluginClasspath()
                .build()

        assertEquals(runner.task(":mergeJar")!!.outcome, TaskOutcome.SUCCESS)
        assertEquals(runner.task(":dexJar")!!.outcome, TaskOutcome.SUCCESS)
    }
}
