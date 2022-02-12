package fr.xpdustry.toxopid.util

import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import java.io.File
import java.net.URL

fun Project.fileProvider(file: File): Provider<RegularFile> =
    layout.file(project.provider { file })

fun URL.downloadTo(file: File): Unit =
    openStream().use { `in` -> file.outputStream().use { `in`.copyTo(it) } }
