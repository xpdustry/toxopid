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

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

open class MindustryExec : DefaultTask() {

    @get:Input
    val runtime: RegularFileProperty = project.objects.fileProperty()

    @get:Input
    val mainClass: Property<String> = project.objects.property(String::class.java)

    @get:Input
    val modsPath: Property<String> = project.objects.property(String::class.java)

    @get:InputFiles
    val artifacts: ConfigurableFileCollection = project.objects.fileCollection()

    @get:Optional
    @get:InputDirectory
    val workingDir: DirectoryProperty = project.objects.directoryProperty()

    init {
        workingDir.convention(project.layout.dir(project.provider { temporaryDir }))
    }

    @TaskAction
    fun runMindustry() {
        val modsDirectory = workingDir.get().asFile.resolve(modsPath.get())

        modsDirectory.mkdirs()
        project.delete(modsDirectory.listFiles()?.filter { it.isFile })
        artifacts.files.forEach {
            it.copyTo(modsDirectory.resolve(it.name))
        }

        project.javaexec {
            it.mainClass.set(mainClass)
            it.workingDir = workingDir.get().asFile
            it.classpath = project.objects.fileCollection().from(runtime)
            it.standardInput = System.`in`
            it.environment["MINDUSTRY_DATA_DIR"] = workingDir.get().asFile.absolutePath
        }
    }
}