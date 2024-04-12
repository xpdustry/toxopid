package com.xpdustry.toxopid.extension

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.hjson.JsonType
import org.hjson.JsonValue as HJSONValue

internal fun HJSONValue.toKotlinJsonElement(): JsonElement =
    when (type!!) {
        JsonType.STRING -> JsonPrimitive(asString())
        JsonType.NUMBER -> JsonPrimitive(asDouble())
        JsonType.BOOLEAN -> JsonPrimitive(asBoolean())
        JsonType.NULL -> JsonNull
        JsonType.ARRAY -> JsonArray(asArray().map(HJSONValue::toKotlinJsonElement))
        JsonType.OBJECT -> JsonObject(asObject().names().associateWith { asObject().get(it).toKotlinJsonElement() })
        JsonType.DSF -> error("I have no idea what DSF means")
    }
