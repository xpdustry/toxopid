package fr.xpdustry.toxopid.util

import groovy.json.JsonSlurper
import java.io.File

/**
 * Utility class to easily represent mod/plugin data.
 */
@Suppress("UNCHECKED_CAST")
data class PluginMetadata(
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
    private constructor(map: Map<String, Any>) : this(
        map["name"]!! as String,
        map["description"]!! as String,
        map["author"]!! as String,
        map["version"]!! as String,
        map["main"]!! as String,
        map["repo"]!! as String?,
        map.getOrDefault("displayName", map["name"]) as String,
        map.getOrDefault("minGameVersion", "v126") as String,
        map.getOrDefault("hidden", true) as Boolean,
        map.getOrDefault("java", true) as Boolean,
        if("dependencies" in map) (map["dependencies"] as Iterable<String>).toList() else emptyList<String>()
    )

    constructor(file: File) : this(JsonSlurper().parseText(file.readText()) as Map<String, Any>)
}
