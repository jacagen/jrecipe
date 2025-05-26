package com.jacagen.jrecipe.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid



enum class Source {
    EVERNOTE,
}

typealias Tag = String

data class Recipe @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class) constructor(
    @ObjectId
    val id: Uuid,

    val title: String,
    val source: Source,
    val author: String?,
    val sourceUrl: String?,
    val content: String,
    val createdInSource: Instant,
    val updatedInSource: Instant,
    val tags: Set<Tag>,
)
