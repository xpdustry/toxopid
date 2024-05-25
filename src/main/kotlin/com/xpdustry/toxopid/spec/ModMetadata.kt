/*
 * This file is part of Toxopid, a Gradle plugin for Mindustry mods/plugins.
 *
 * MIT License
 *
 * Copyright (c) 2024 Xpdustry
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
import com.xpdustry.toxopid.extension.toKotlinJsonElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.hjson.JsonObject
import java.io.File

/**
 * Represents the metadata of a mod.
 */
@Serializable
public data class ModMetadata(
    public var name: String = "",
    public var displayName: String = "",
    public var description: String = "",
    public var subtitle: String = "",
    public var author: String = "",
    public var version: String = "",
    @SerialName("main")
    public var mainClass: String = "",
    @SerialName("repo")
    public var repository: String = "",
    public var minGameVersion: String = Toxopid.DEFAULT_MINDUSTRY_VERSION,
    public var hidden: Boolean = false,
    public var java: Boolean = true,
    public var keepOutlines: Boolean = false,
    @SerialName("texturescale")
    public var textureScale: Float = 1f,
    public var pregenerated: Boolean = false,
    public val dependencies: MutableList<ModDependency> = mutableListOf(),
    public val softDependencies: MutableList<ModDependency> = mutableListOf(),
) {
    public companion object {
        private val JSON =
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            }

        public fun fromJson(json: String): ModMetadata =
            JSON.decodeFromJsonElement<ModMetadata>(JsonObject.readHjson(json).toKotlinJsonElement())

        public fun fromJson(file: File): ModMetadata = fromJson(file.readText())
    }

    /**
     * @return a pretty printed json representation of this [ModMetadata]
     */
    public fun toJson(): String = JSON.encodeToString(this)
}
