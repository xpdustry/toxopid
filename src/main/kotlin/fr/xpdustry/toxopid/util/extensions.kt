package fr.xpdustry.toxopid.util

import org.gradle.api.plugins.ExtensionContainer
import java.io.File
import java.net.URL

fun URL.downloadTo(file: File): Unit =
    openStream().use { `in` -> file.outputStream().use { `in`.copyTo(it) } }

fun <T> ExtensionContainer.findOrCreate(name: String, type: Class<T>, vararg arguments: Any): T =
    findByType(type) ?: create(name, type, *arguments)
