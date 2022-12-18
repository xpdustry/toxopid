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
package fr.xpdustry.toxopid.task

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.JavaExec
import java.io.File
import java.nio.file.Files
import java.util.zip.ZipFile

/**
 * Starts a Mindustry instance, blocks the build process until closing.
 * Every jar and zip mods are deleted every time its run so make sure you include
 * them in [MindustryExec.mods] and not directly in the [MindustryExec.workingDir].
 */
open class MindustryExec : JavaExec() {

    companion object {
        val MOD_METADATA_FILE = Regex("(mod|plugin)\\.h?json")
    }

    /**
     * The directory where the game loads the mods, relative to the [MindustryExec.getWorkingDir].
     *
     * **Only modify if you know what you are doing.**
     */
    @get:Input
    val modsPath: Property<String> = project.objects.property(String::class.java)

    /**
     * The mods to load.
     */
    @get:InputFiles
    val mods: ConfigurableFileCollection = project.objects.fileCollection()

    init {
        workingDir = temporaryDir
    }

    override fun exec() {
        environment("MINDUSTRY_DATA_DIR", workingDir)
        logger.debug("MINDUSTRY_DATA_DIR: $workingDir")

        val modsDirectory = workingDir.resolve(modsPath.get())
        modsDirectory.mkdirs()

        for (file in modsDirectory.listFiles()!!) {
            if (isValidModArchive(file) || isValidModDirectory(file)) {
                logger.debug("Deleting mod: $file")
                project.delete(file)
            }
        }
        for (file in mods.files) {
            if (isValidModArchive(file) || isValidModDirectory(file)) {
                logger.debug("Copying mod: $file")
                project.copy {
                    it.from(file)
                    it.into(modsDirectory)
                }
            } else {
                logger.warn("Invalid mod file detected: $file")
            }
        }

        super.exec()
    }

    private fun isValidModDirectory(file: File) =
        file.isDirectory && Files.walk(file.toPath()).anyMatch { MOD_METADATA_FILE.matches(it.fileName.toString()) }

    private fun isValidModArchive(file: File) =
        file.isFile && (file.extension == "jar" || file.extension == "zip") && ZipFile(file).use { zip ->
            zip.entries().asSequence().any { MOD_METADATA_FILE.matches(it.name) }
        }
}
