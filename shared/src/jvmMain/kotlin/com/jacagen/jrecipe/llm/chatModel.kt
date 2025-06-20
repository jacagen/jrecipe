@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.llm

import dev.langchain4j.model.chat.Capability
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiEmbeddingModel
import kotlin.uuid.ExperimentalUuidApi

val model = OpenAiChatModel.builder().apiKey(apiKey).modelName("gpt-4o").logResponses(true)
    .supportedCapabilities(setOf(Capability.RESPONSE_FORMAT_JSON_SCHEMA))
    .strictJsonSchema(true)     // Required for OpenAI (not necessarily others)
    .logRequests(true)  // What is this for?
    .logResponses(true)
    .temperature(0.9).build()

val embeddingModel = OpenAiEmbeddingModel.builder()
    .apiKey(apiKey)
    .modelName("text-embedding-3-small")
    .build()

