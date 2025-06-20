@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.llm.serde

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UuidDeserializer : JsonDeserializer<Uuid>() { // should go into serde
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Uuid {
        val uuidString = p.valueAsString
        return Uuid.Companion.parse(uuidString)
    }
}