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

private val evernoteInstantFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

private val systemMessage = systemMessage()

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

    internal suspend fun toRecipe(): Recipe {
        val request = ChatRequest.builder().messages(
            systemMessage, UserMessage(
                """
                    Please format the following recipe as instructed.
                    Set its `id` field to `$_id`.
                    Set its `title` field to `$title`.
                    Set its `source` field to `EVERNOTE`.
                """.trimIndent()
            ), UserMessage(content)
        )
        val response = model.chat(
            request
        )
        val jsonText = response.aiMessage().text()  // JSON string--but what if it's not??
        println(jsonText)
        val typeRef = object : TypeReference<Recipe>() {}
        val recipe = objectMapper.readValue(jsonText, typeRef)
        println("Processed recipe $title")
        return recipe.copy(
            id = Uuid.parse(_id),
            title = title,
            source = "EVERNOTE", //RecipeSource.EVERNOTE,
            author = author,
            sourceUrl = sourceUrl,
            createdInSource = created.toKotlinInstant(),
            updatedInSource = updated.toKotlinInstant(),
            tags = tags,
        )
    }
}

fun main() = runBlocking {
    loadEvernoteToMongo()
    loadMongoEvernoteToRecipes()
}

private fun parseEnexFile(file: File): List<EvernoteNote> {
    val notes = mutableListOf<EvernoteNote>()

    val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc = docBuilder.parse(file)
    val noteNodes = doc.getElementsByTagName("note")

    for (i in 0 until noteNodes.length) {
        val noteElement = noteNodes.item(i) as Element

        var author: String? = null
        var source: String? = null
        var sourceUrl: String? = null
        var sourceApplication: String? = null
        var subjectDate: String? = null
        var contentClass: String? = null

        // Parse <note-attributes> children into key-value pairs
        val noteAttrNodes = noteElement.getElementsByTagName("note-attributes")
        if (noteAttrNodes.length > 0) {
            val noteAttrElem = noteAttrNodes.item(0) as Element
            val children = noteAttrElem.childNodes
            for (j in 0 until children.length) {
                val child = children.item(j)
                if (child is Element) {
                    when (child.tagName) {
                        "author" -> author = child.textContent.trim()
                        "source" -> source = child.textContent.trim()
                        "source-url" -> sourceUrl = child.textContent.trim()
                        "source-application" -> sourceApplication = child.textContent.trim()
                        "subject-date" -> subjectDate = child.textContent.trim()
                        "content-class" -> contentClass = child.textContent.trim()
                    }
                }
            }
        }

        val title = noteElement.getElementsByTagName("title").item(0).textContent.trim()
        val createdText = noteElement.getElementsByTagName("created").item(0).textContent.trim()
        val updatedText = noteElement.getElementsByTagName("updated").item(0).textContent.trim()
        val created = parseEvernoteTimestamp(createdText)
        val updated = parseEvernoteTimestamp(updatedText)
        val content = noteElement.getElementsByTagName("content").item(0).textContent.trim()


        val tagNodes = noteElement.getElementsByTagName("tag")
        val tags = mutableSetOf<String>()
        for (j in 0 until tagNodes.length) {
            tags.add(tagNodes.item(j).textContent.trim())
        }

        notes.add(
            EvernoteNote(
                title = title,
                created = created,
                updated = updated,
                author = author,
                source = source,
                sourceUrl = sourceUrl,
                sourceApplication = sourceApplication,
                subjectDate = subjectDate,
                contentClass = contentClass,
                content = content,
                tags = tags,
            )
        )
    }

    return notes
}

private fun parseEvernoteTimestamp(ts: String): Instant {
    return LocalDateTime.parse(ts, evernoteInstantFormatter).toInstant(ZoneOffset.UTC)
}

private suspend fun loadEvernoteToMongo() {
    val resource =
        EvernoteNote::class.java.classLoader.getResource("recipeSource/Recipes.enex") ?: error("Resource not found")
    val file = File(resource.toURI())
    val notes = parseEnexFile(file)
    saveNotesToMongo(notes)
}

internal suspend fun saveNotesToMongo(notes: List<EvernoteNote>) {
    val collection = database.getCollection<EvernoteNote>("evernote")

    // Remove all existing documents
    collection.deleteMany(Document())  // or Filters.empty()

    // Insert new notes
    collection.insertMany(notes)

    println("Inserted ${notes.size} Evernote notes into MongoDB.")
}


private suspend fun loadMongoEvernoteToRecipes() {
    //recipeDao.deleteAll()
    val evernoteCollection = database.getCollection<EvernoteNote>("evernote")
    evernoteCollection.find()
        .filter { recipeDao.findById(Uuid.parse(it._id)) == null }
        .map { evernote ->
            evernote.toRecipe()
        }.collect { recipeDao.insert(it) }
}

private fun systemMessage(): SystemMessage {
    val schemaGen = JsonSchemaGenerator(objectMapper)
    val jsonSchema = schemaGen.generateSchema(Recipe::class.java)
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
        For each recipe always return an `ingredients` list--if you can not figure out any ingredients, return the empty list ([]).
        If there is no valid value for the `content` field, just make something up yourself.
    """.trimIndent()
    return SystemMessage(jsonInstruction)
}
