@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package com.jacagen.jrecipe.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class EvernoteNoteSummary(
    @param:ObjectId
    @property:ObjectId
    val id: String = Uuid.random().toString(),    // Using real uuids turns out to be a giant pain in the neck

    val title: String,
)

data class EvernoteNote(
    @param:ObjectId
    @property:ObjectId
    val id: String = Uuid.random().toString(),    // Using real uuids turns out to be a giant pain in the neck

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





