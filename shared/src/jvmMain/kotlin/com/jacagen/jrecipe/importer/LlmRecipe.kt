@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.importer

import com.jacagen.jrecipe.importer.evernote.llmRecipeCollection
import com.jacagen.jrecipe.model.InstantIso8601Serializer
import com.jacagen.jrecipe.model.ObjectId
import com.jacagen.jrecipe.model.Tag
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal data class LlmRecipe @OptIn(ExperimentalTime::class) constructor(
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

internal class

internal suspend fun llmRecipeExists(id: Uuid) = llmRecipeCollection.find(Filters.eq("_id", id)).firstOrNull() != null