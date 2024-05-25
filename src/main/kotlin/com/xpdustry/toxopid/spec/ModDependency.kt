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
