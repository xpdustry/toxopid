package fr.xpdustry.toxopid.util

import org.hjson.JsonObject
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
    val minGameVersion: String = "v126.2",
    val hidden: Boolean = true,
    val java: Boolean = true,
    val dependencies: List<String> = emptyList()
) {
    private constructor(json: JsonObject) : this(
        json["name"]!!.asString(),
        json["description"]!!.asString(),
        json["author"]!!.asString(),
        json["version"]!!.asString(),
        json["main"]!!.asString(),
        json["repo"]?.asString(),
        json.getString("displayName", json["name"].asString()),
        json.getString("minGameVersion", "v126.2"),
        json.getBoolean("hidden", true),
        json.getBoolean("java", true),
        json["dependencies"]?.asArray()?.map { it.asString()!! } ?: emptyList()
    )

    constructor(file: File) : this(JsonValue.readHjson(file.readText()).asObject())
}
