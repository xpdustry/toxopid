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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Creates a simple mod dependency.
 */
public fun dependency(name: String): ModDependency = ModDependency.Simple(name)

/**
 * Represents a mod dependency.
 */
@Serializable
public sealed interface ModDependency {
    /**
     * The name of the mod.
     */
    public val name: String

    /**
     * Dependency only containing the name of the mod.
     */
    @Serializable(SimpleModDependencySerializer::class)
    public data class Simple(override val name: String) : ModDependency
}

private object SimpleModDependencySerializer : KSerializer<ModDependency.Simple> {
    override val descriptor = PrimitiveSerialDescriptor("ModDependency.Simple", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) = ModDependency.Simple(decoder.decodeString())

    override fun serialize(
        encoder: Encoder,
        value: ModDependency.Simple,
    ) = encoder.encodeString(value.name)
}
