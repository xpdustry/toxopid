package fr.xpdustry.toxopid.util

import java.io.File
import java.net.URL

fun URL.downloadTo(file: File): Unit =
    openStream().use { `in` -> file.outputStream().use { `in`.copyTo(it) } }
