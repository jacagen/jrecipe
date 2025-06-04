@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.importer.notes

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator
import com.jacagen.jrecipe.dao.mongodb.database
import com.jacagen.jrecipe.importer.evernote.LlmRecipe
import com.jacagen.jrecipe.importer.evernote.llmRecipeCollection
import com.jacagen.jrecipe.llm.model
import com.jacagen.jrecipe.llm.objectMapper
import com.mongodb.client.model.Filters.eq
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.kotlin.model.chat.chat
import dev.langchain4j.model.chat.request.ChatRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val systemMessage = systemMessage()

internal suspend fun parseAppleNotesRecipesWithLlm() {
    val notesCollection = database.getCollection<AppleNoteRecipe>("apple-notes")
    notesCollection.find().filter { !notesRecipeExists(it._id) }.collect { appleNote ->
        delay(5000) // Someday add more graceful rate limiting (see https://chatgpt.com/share/683f2059-1c48-8003-bcfc-59a8795d5785)
        try {
            val recipe = appleNote.toLlmRecipe()
            llmRecipeCollection.insertOne(recipe)
        } catch (x: Throwable) {
            println("ERROR: Could not save recipe ${appleNote.title}")
            x.printStackTrace()
        }
    }
}

private suspend fun notesRecipeExists(id: Uuid) = llmRecipeCollection.find(eq("_id", id)).firstOrNull() != null

private suspend fun AppleNoteRecipe.toLlmRecipe(): LlmRecipe {
    val request = ChatRequest.builder().messages(
        systemMessage, UserMessage(
            """
                    Please format the following recipe as instructed.
                    Set its `source` field to `APPLE_NOTE`.
                """.trimIndent()
        ), UserMessage(objectMapper.writeValueAsString(this))
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

@Serializable
data class AppleNoteRecipe(
    val _id: Uuid,
    val title: String,
    val tags: List<String>,
    val sourceUrl: String? = null,
    val ingredients: List<Ingredient>,
    val tools: List<String> = emptyList(),
    val steps: List<String>,
    val prepTimeMinutes: Int? = null,
    val cookTimeMinutes: Int? = null,
    val calories: Int? = null,
    val yield: String? = null,
    val notes: String? = null
)

@Serializable
data class Ingredient(
    val name: String,
    val amount: String,
    val notes: String? = null,
    val optional: Boolean = false
)

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
        If there is any information about "techniques" in the recipe, also include this in the `notes` field (along with any other notes).
        For any field expressing a duration (such as `time`), express it as the number of seconds since the UNIX epoch start.
    """.trimIndent()
    return SystemMessage(jsonInstruction)
}