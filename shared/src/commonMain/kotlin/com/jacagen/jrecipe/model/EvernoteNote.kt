@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package com.jacagen.jrecipe.model

import com.jacagen.jrecipe.serde.InstantIso8601Serializer
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class EvernoteNoteSummary(
    @ObjectId val id: String = Uuid.random().toString(),    // Using real Uuids turns out to be a giant pain in the neck
    val title: String,
)

data class EvernoteNote(
    @ObjectId val id: String = Uuid.random().toString(),    // Using real Uuids turns out to be a giant pain in the neck
    val title: String,
    @Serializable(with = InstantIso8601Serializer::class) val created: Instant,
    @Serializable(with = InstantIso8601Serializer::class) val updated: Instant,
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





