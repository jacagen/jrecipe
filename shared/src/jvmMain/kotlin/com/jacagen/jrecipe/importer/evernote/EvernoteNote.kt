@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package com.jacagen.jrecipe.importer.evernote


import com.jacagen.jrecipe.dao.mongodb.database
import com.jacagen.jrecipe.model.Tag
import org.bson.Document
import java.time.Instant
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

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





