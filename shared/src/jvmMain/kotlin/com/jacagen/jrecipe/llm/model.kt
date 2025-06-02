@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.llm

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.langchain4j.model.chat.Capability
import dev.langchain4j.model.openai.OpenAiChatModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// Need to move this!
const val apiKey =
    "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"

class UuidDeserializer : JsonDeserializer<Uuid>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Uuid {
        val uuidString = p.valueAsString
        return Uuid.parse(uuidString)
    }
}

val uuidModule = SimpleModule().apply {
    addDeserializer(Uuid::class.java, UuidDeserializer())
}

val model = OpenAiChatModel.builder().apiKey(apiKey).modelName("gpt-4o").logResponses(true)
    .supportedCapabilities(setOf(Capability.RESPONSE_FORMAT_JSON_SCHEMA))
    .strictJsonSchema(true)     // Required for OpenAI (not necessarily others)
    .logRequests(true)  // What is this for?
    .logResponses(true).build()

internal val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(uuidModule)