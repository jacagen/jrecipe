@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.importer.evernote

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator
import com.jacagen.jrecipe.dao.mongodb.database
import com.jacagen.jrecipe.llm.model
import com.jacagen.jrecipe.llm.objectMapper
import com.jacagen.jrecipe.model.InstantIso8601Serializer
import com.jacagen.jrecipe.model.ObjectId
import com.jacagen.jrecipe.model.Tag
import com.mongodb.client.model.Filters.eq
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.kotlin.model.chat.chat
import dev.langchain4j.model.chat.request.ChatRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal val llmRecipeCollection = database.getCollection<LlmRecipe>("llm-recipe")

internal data class LlmRecipe @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class) constructor(
    @ObjectId val _id: Uuid,

    val title: String,
    val source: String,
    val author: String?,
    val sourceUrl: String?,
    val yield: String?,
    val time: String?,
    val ingredients: List<String>?,
    val notes: String?,
    val steps: List<String>?,
    @Serializable(with = InstantIso8601Serializer::class) val createdInSource: Instant?,
    @Serializable(with = InstantIso8601Serializer::class) val updatedInSource: Instant?,
    val tags: Set<Tag>,
)

private val systemMessage = systemMessage()

private fun systemMessage(): SystemMessage {
    val schemaGen = JsonSchemaGenerator(objectMapper)
    val jsonSchema = schemaGen.generateSchema(LlmRecipe::class.java)
    val schemaNode: JsonNode = objectMapper.valueToTree(jsonSchema)
    val props = schemaNode.get("properties")    // Ever null????

    val fields = props.fieldNames().asSequence().map { name ->
        val typeNode = props.get(name)
        val typeStr = typeNode?.get("type")?.asText() ?: "unknown"
        "`$name` ($typeStr)"
    }.joinToString(", ")
    val jsonInstruction = """
        You are a JSON API. 
        You will be given a recipe described in HTML, and your job is to parse that recipe and convert it to JSON.
        Here are the available JSON fields for you to use: $fields.
        You do not need to fill in all of the fields.   
        Respond only with raw JSON. Do not include explanations, formatting, or commentary.  BE SURE THE JSON IS WELL-FORMED!
        Do not wrap your response in triple-backticks.
        Do not include any HTML in field values--if there is a field that contains formatted text, please convert the HTML to Markdown.
        For any fields which contain date/times: please return them as a structure with two fields: `epochSeconds` and `nanosecondsOfSecond`.
        Return an empty list ([]) for the value of `tags`.
        Always set the `source` field to `EVERNOTE`.
    """.trimIndent()
    return SystemMessage(jsonInstruction)
}

private suspend fun llmRecipeExists(id: Uuid) =
    llmRecipeCollection.find(eq("_id", id)).firstOrNull() != null


internal suspend fun parseEvernoteRecipesWithLlm() {
    val evernoteCollection = database.getCollection<EvernoteNote>("evernote")
    evernoteCollection.find()
        .limit(1)
        .filter { !llmRecipeExists(Uuid.parse(it._id)) }
        .collect { evernote ->
            delay(5000) // Someday add more graceful rate limiting (see https://chatgpt.com/share/683f2059-1c48-8003-bcfc-59a8795d5785)
            val recipe = evernote.toLlmRecipe()
            llmRecipeCollection.insertOne(recipe)
        }
}

private suspend fun EvernoteNote.toLlmRecipe(): LlmRecipe {
    val request = ChatRequest.builder().messages(
        systemMessage, UserMessage(
            """
                    Please format the following recipe as instructed.
                    Set its `id` field to `$_id`.
                    Set its `title` field to `$title`.
                    Set its `source` field to `EVERNOTE`.
                    Set its `sourceUrl` field to ${if (sourceUrl != null) null else "`$sourceUrl`"}.
                    Set its `tags` to the following list: ${tags.joinToString(", ") { "`$it`" }}.
                """.trimIndent()
        ), UserMessage(content)
    )
    val response = model.chat(
        request
    )
    val jsonText = response.aiMessage().text()  // JSON string--but what if it's not??
    println(jsonText)
    val typeRef = object : TypeReference<LlmRecipe>() {}
    val recipe = objectMapper.readValue(jsonText, typeRef)
    println("Processed recipe $title")
    return recipe
}