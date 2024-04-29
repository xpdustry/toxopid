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
package com.xpdustry.toxopid.task

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.property
import java.io.File
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.createDirectories
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.notExists

/**
 * Starts a Mindustry instance, blocks the build process until closing.
 * Every jar and zip mods are deleted every time its run so make sure you include
 * them in [MindustryExec.mods] and not directly in the [MindustryExec.workingDir].
 */
public open class MindustryExec : JavaExec() {
    /**
     * The directory where the game loads the mods, relative to the [MindustryExec.getWorkingDir].
     *
     * **Only modify if you know what you are doing.**
     */
    @get:Input
    public val modsPath: Property<Path> = project.objects.property()

    /**
     * The mods to load.
     */
    @get:InputFiles
    public val mods: ConfigurableFileCollection = project.objects.fileCollection()

    init {
        workingDir = temporaryDir
    }

    override fun exec() {
        logger.info("Starting Mindustry instance in $workingDir")
        environment("MINDUSTRY_DATA_DIR", workingDir)

        val modsDirectory = workingDir.toPath().resolve(modsPath.get())
        if (modsDirectory.notExists()) {
            modsDirectory.createDirectories()
        }

        for (file in modsDirectory.listDirectoryEntries()) {
            if (isValidMod(file)) {
                logger.debug("Deleting mod: {}", file)
                project.delete(file)
            }
        }

        for (file in mods.files.map(File::toPath)) {
            if (isValidMod(file)) {
                logger.debug("Copying mod: {}", file)
                project.copy {
                    from(file)
                    into(modsDirectory)
                }
            }
        }

        super.exec()
    }

    private fun isValidMod(file: Path): Boolean {
        if (file.isDirectory()) {
            return file.listDirectoryEntries().any { MOD_METADATA_FILE.matches(it.fileName.toString()) }
        }
        if (file.isRegularFile() && (file.extension == "jar" || file.extension == "zip")) {
            return ZipFile(file.toFile()).use { zip ->
                zip.entries().asSequence().any { MOD_METADATA_FILE.matches(it.name) }
            }
        }
        return false
    }

    public companion object {
        public val MOD_METADATA_FILE: Regex = Regex("(mod|plugin)\\.h?json")
        public const val DESKTOP_EXEC_TASK_NAME: String = "runMindustryDesktop"
        public const val SERVER_EXEC_TASK_NAME: String = "runMindustryServer"
    }
}
