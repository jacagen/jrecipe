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