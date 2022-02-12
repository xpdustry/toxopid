package fr.xpdustry.toxopid.util

import org.gradle.internal.impldep.com.google.gson.Gson
import org.hjson.JsonValue
import java.io.File

/** Utility class to easily represent mod/plugin data. */
data class ModMetadata(
    val name: String,
    val description: String,
    val author: String,
    val version: String,
    val main: String,
    val repo: String?,
    val displayName: String = name,
    val minGameVersion: String = "v126",
    val hidden: Boolean = true,
    val java: Boolean = true,
    val dependencies: List<String> = emptyList()
) {
    companion object {
        private val GSON = Gson()

        @JvmStatic
        fun of(file: File): ModMetadata =
            GSON.fromJson(JsonValue.readHjson(file.readText()).toString(), ModMetadata::class.java)
    }
}
