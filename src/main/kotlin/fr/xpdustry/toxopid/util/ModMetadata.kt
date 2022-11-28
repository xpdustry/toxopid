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
package fr.xpdustry.toxopid.util

import groovy.json.JsonOutput
import org.hjson.JsonObject
import java.io.File

data class ModMetadata(
    var name: String = "",
    var displayName: String = "",
    var description: String = "",
    var author: String = "",
    var version: String = "",
    var main: String = "",
    var repo: String = "",
    var minGameVersion: String = "140",
    var hidden: Boolean = false,
    var java: Boolean = true,
    val dependencies: MutableList<String> = mutableListOf()
) {
    companion object {
        fun fromJson(json: String) = JsonObject
            .readHjson(json)
            .asObject()
            .run {
                ModMetadata(
                    getString("name", ""),
                    getString("displayName", ""),
                    getString("description", ""),
                    getString("author", ""),
                    getString("version", ""),
                    getString("main", ""),
                    getString("repo", ""),
                    getString("minGameVersion", ""),
                    getBoolean("hidden", false),
                    getBoolean("java", true),
                    get("dependencies")?.asArray()?.map { it.asString() }?.toMutableList()
                        ?: mutableListOf()
                )
            }

        fun fromJson(file: File) = fromJson(file.readText())
    }

    /**
     * @param pretty whether the json string should be pretty printed or not.
     * @return a json representation of this [ModMetadata]
     */
    fun toJson(pretty: Boolean = true): String {
        val json = JsonOutput.toJson(this)
        return if (pretty) JsonOutput.prettyPrint(json) else json
    }
}
