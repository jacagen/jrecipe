package com.jacagen.jrecipe.llm.serde

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import kotlinx.datetime.LocalTime

class LocalTimeDeserializer : JsonDeserializer<LocalTime>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalTime {
        return LocalTime.Companion.parse(p.text)
    }
}