package com.jacagen.jrecipe.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


data class Recipe @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class) constructor(
    @ObjectId val id: Uuid,

    val title: String,
    val source: RecipeSource,
    val author: String?,
    val sourceUrl: String?,
    val content: String,
    val createdInSource: Instant,
    val updatedInSource: Instant,
    val tags: Set<Tag>,
)
