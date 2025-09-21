@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.llm

import dev.langchain4j.model.chat.Capability
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiEmbeddingModel
import java.time.Duration
import kotlin.uuid.ExperimentalUuidApi

val model: OpenAiChatModel = OpenAiChatModel.builder().apiKey(apiKey).modelName("gpt-4o")
    .supportedCapabilities(setOf(Capability.RESPONSE_FORMAT_JSON_SCHEMA))
    .strictJsonSchema(true) // Required for OpenAI (not necessarily others)
    .logRequests(true)
    .logResponses(true)
    .timeout(Duration.ofSeconds(60))
    .temperature(0.9).build()

val embeddingModel: OpenAiEmbeddingModel = OpenAiEmbeddingModel.builder()
    .apiKey(apiKey)
    .modelName("text-embedding-3-small")
    .build()

