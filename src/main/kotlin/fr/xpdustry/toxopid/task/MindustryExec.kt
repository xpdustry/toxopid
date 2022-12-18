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
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

/**
 * Starts a Mindustry instance, blocks the build process until closing.
 * Every jar and zip mods are deleted every time its run so make sure you include
 * them in [MindustryExec.mods] and not directly in the [MindustryExec.workingDir].
 */
open class MindustryExec : JavaExec() {

    /**
     * The classpath of this Mindustry instance.
     *
     * **Only modify if you know what you are doing.**
     */
    @get:InputFiles
    val classpath: ConfigurableFileCollection = project.objects.fileCollection()

    /**
     * The directory where the game loads the mods, relative to the [MindustryExec.workingDir].
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

    /**
     * The arguments to pass to the Mindustry instance for startup commands.
     */
    @get:Optional
    @get:Input
    val args: ListProperty<String> = project.objects.listProperty(String::class.java)

    /**
     * The JVM arguments to pass to the Mindustry instance.
     *
     * *Use it to attach a debugger.*
     */
    @get:Optional
    @get:Input
    val jvmArgs: ListProperty<String> = project.objects.listProperty(String::class.java)

    init {
        workingDir = project.layout.dir(project.provider { temporaryDir }).get().asFile
        args.convention(listOf())
        jvmArgs.convention(listOf())
    }

    @TaskAction
    fun runMindustry() {
        val modsDirectory = workingDir.resolve(modsPath.get())

        modsDirectory.mkdirs()
        project.delete(modsDirectory.listFiles()?.filter(::isValidMod))
        mods.files.forEach {
            if (!isValidMod(it)) throw IllegalArgumentException("MindustryExec mods only support jar and zip files.")
            it.copyTo(modsDirectory.resolve(it.name))
        }

        mainClass.set(project.objects.property(String::class.java))

        standardInput = System.`in`
        environment["MINDUSTRY_DATA_DIR"] = workingDir.absolutePath
        args(args.get())
        jvmArgs(jvmArgs.get())
    }

    private fun isValidMod(file: File) =
            file.isFile && (file.extension == "jar" || file.extension == "zip")
}
