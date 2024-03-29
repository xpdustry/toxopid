/*
 * This file is part of Toxopid, a Gradle plugin for Mindustry mods/plugins.
 *
 * MIT License
 *
 * Copyright (c) 2023 Xpdustry
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
package fr.xpdustry.toxopid.spec

import fr.xpdustry.toxopid.Toxopid
import groovy.json.JsonOutput
import org.hjson.JsonObject
import java.io.File

public data class ModMetadata(
    public var name: String = "",
    public var displayName: String = "",
    public var description: String = "",
    public var subtitle: String = "",
    public var author: String = "",
    public var version: String = "",
    public var main: String = "",
    public var repo: String = "",
    public var minGameVersion: String = Toxopid.DEFAULT_MINDUSTRY_VERSION,
    public var hidden: Boolean = false,
    public var java: Boolean = true,
    public var keepOutlines: Boolean = false,
    public var texturescale: Float = 1f,
    public var pregenerated: Boolean = false,
    public val dependencies: MutableList<String> = mutableListOf(),
    public val softDependencies: MutableList<String> = mutableListOf()
) {
    public companion object {
        public fun fromJson(json: String): ModMetadata = JsonObject
            .readHjson(json)
            .asObject()
            .run {
                ModMetadata(
                    getString("name", ""),
                    getString("displayName", ""),
                    getString("description", ""),
                    getString("subtitle", ""),
                    getString("author", ""),
                    getString("version", ""),
                    getString("main", ""),
                    getString("repo", ""),
                    getString("minGameVersion", ""),
                    getBoolean("hidden", false),
                    getBoolean("java", true),
                    getBoolean("keepOutlines", false),
                    getFloat("texturescale", 1f),
                    getBoolean("pregenerated", false),
                    get("dependencies")?.asArray()?.map { it.asString() }?.toMutableList()
                        ?: mutableListOf(),
                    get("softDependencies")?.asArray()?.map { it.asString() }?.toMutableList()
                        ?: mutableListOf()
                )
            }
        public fun fromJson(file: File): ModMetadata = fromJson(file.readText())
    }

    /**
     * @param pretty whether the json string should be pretty printed or not.
     * @return a json representation of this [ModMetadata]
     */
    public fun toJson(pretty: Boolean = true): String {
        val json = JsonOutput.toJson(this)
        return if (pretty) JsonOutput.prettyPrint(json) else json
    }
}
