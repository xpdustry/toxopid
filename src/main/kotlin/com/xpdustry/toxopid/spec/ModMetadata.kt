/*
 * This file is part of Toxopid, a Gradle plugin for Mindustry mods/plugins.
 *
 * MIT License
 *
 * Copyright (c) 2025 Xpdustry
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
package com.xpdustry.toxopid.spec

import com.xpdustry.toxopid.Toxopid
import java.io.File
import java.io.Serializable
import org.hjson.JsonArray
import org.hjson.JsonObject
import org.hjson.Stringify

/** Represents the metadata of a mod. */
public data class ModMetadata(
    public var name: String = "",
    public var displayName: String = "",
    public var description: String = "",
    public var subtitle: String = "",
    public var author: String = "",
    public var version: String = "",
    public var mainClass: String = "",
    public var repository: String = "",
    public var minGameVersion: String = Toxopid.DEFAULT_MINDUSTRY_VERSION,
    public var hidden: Boolean = false,
    public var java: Boolean = true,
    public var keepOutlines: Boolean = false,
    public var textureScale: Float = 1f,
    public var pregenerated: Boolean = false,
    public val dependencies: MutableList<ModDependency> = mutableListOf(),
) : Serializable {
    public companion object {
        /** @return a parsed [ModMetadata] from json */
        @JvmStatic public fun fromJson(json: String): ModMetadata = fromJson(JsonObject.readHjson(json).asObject())

        /** @return a parsed [ModMetadata] from a file */
        @JvmStatic
        public fun fromJson(file: File): ModMetadata =
            file.reader().use { fromJson(JsonObject.readHjson(it).asObject()) }

        /** @return a pretty printed json representation of a [ModMetadata] */
        @JvmStatic
        public fun toJson(metadata: ModMetadata, pretty: Boolean = true): String =
            JsonObject()
                .add("name", metadata.name)
                .add("displayName", metadata.displayName)
                .add("description", metadata.description)
                .add("subtitle", metadata.subtitle)
                .add("author", metadata.author)
                .add("version", metadata.version)
                .add("main", metadata.mainClass)
                .add("repo", metadata.repository)
                .add("minGameVersion", metadata.minGameVersion)
                .add("hidden", metadata.hidden)
                .add("java", metadata.java)
                .add("keepOutlines", metadata.keepOutlines)
                .add("texturescale", metadata.textureScale)
                .add("pregenerated", metadata.pregenerated)
                .add(
                    "dependencies",
                    JsonArray().apply { metadata.dependencies.filter { !it.soft }.forEach { add(it.name) } },
                )
                .add(
                    "softDependencies",
                    JsonArray().apply { metadata.dependencies.filter { it.soft }.forEach { add(it.name) } },
                )
                .toString(if (pretty) Stringify.FORMATTED else Stringify.PLAIN)

        private fun fromJson(json: JsonObject) =
            ModMetadata(
                name = json.getString("name", ""),
                displayName = json.getString("displayName", ""),
                description = json.getString("description", ""),
                subtitle = json.getString("subtitle", ""),
                author = json.getString("author", ""),
                version = json.getStringOrInt("version", ""),
                mainClass = json.getString("main", ""),
                repository = json.getString("repo", ""),
                minGameVersion = json.getStringOrInt("minGameVersion", Toxopid.DEFAULT_MINDUSTRY_VERSION),
                hidden = json.getBoolean("hidden", false),
                java = json.getBoolean("java", true),
                keepOutlines = json.getBoolean("keepOutlines", false),
                textureScale = json.getFloat("texturescale", 1f),
                pregenerated = json.getBoolean("pregenerated", false),
                dependencies =
                    (json.readDependencies("dependencies", false) + json.readDependencies("softDependencies", true))
                        .toMutableList(),
            )

        private fun JsonObject.readDependencies(name: String, soft: Boolean): List<ModDependency> {
            val dependencies = get(name) ?: return emptyList()
            return dependencies.asArray().map {
                if (it.isString) ModDependency(it.asString(), soft) else error("Unexpected dependency: $it")
            }
        }

        private fun JsonObject.getStringOrInt(name: String, def: String): String {
            val value = get(name)
            return when {
                value == null -> def
                value.isString -> value.asString()
                value.isNumber -> value.toString()
                else -> error("$name is not a string nor number: $value")
            }
        }
    }
}
