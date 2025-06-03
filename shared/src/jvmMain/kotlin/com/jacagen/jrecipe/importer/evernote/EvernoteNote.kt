@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package com.jacagen.jrecipe.importer.evernote


import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator
import com.jacagen.jrecipe.dao.mongodb.database
import com.jacagen.jrecipe.dao.mongodb.recipeDao
import com.jacagen.jrecipe.llm.model
import com.jacagen.jrecipe.llm.objectMapper
import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.model.Tag
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.kotlin.model.chat.chat
import dev.langchain4j.model.chat.request.ChatRequest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.w3c.dom.Element
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal data class EvernoteNote(
    @Suppress("PropertyName") val _id: String = UUID.randomUUID().toString(),
    val title: String,
    val created: Instant,
    val updated: Instant,
    val author: String?,
    val source: String?,
    val sourceUrl: String?,
    val sourceApplication: String?,
    val subjectDate: String?,
    val contentClass: String?,
    val content: String,
    val tags: Set<Tag>,
) {
    @Suppress("unused")
    fun dump() {
        println("Title: $title")
        println("Created: $created")
        println("Updated: $updated")
        println("Author: $author")
        println("Source: $source")
        println("SourceUrl: $sourceUrl")
        println("SourceApplication: $sourceApplication")
        println("Subject date: $subjectDate")
        println("Content class: $contentClass")
        println("Tags: ${tags.joinToString()}")
        println("Content: ${content.take(100)}...")
        println("---")
    }
}







internal suspend fun saveNotesToMongo(notes: List<EvernoteNote>) {
    val collection = database.getCollection<EvernoteNote>("evernote")

    // Remove all existing documents
    collection.deleteMany(Document())  // or Filters.empty()

    // Insert new notes
    collection.insertMany(notes)

    println("Inserted ${notes.size} Evernote notes into MongoDB.")
}





