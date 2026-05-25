package com.jacagen.jrecipe.model

import com.jacagen.jrecipe.serde.InstantIso8601Serializer
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

typealias RecipeId = String

@Serializable
data class RecipeSummary(
    @param:ObjectId
    @property:ObjectId
    val id: RecipeId,

    val title: String,
    val source: String,
    val tags: Set<Tag>,
)

@Serializable
data class Recipe @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class) constructor(

    @param:ObjectId
    @property:ObjectId
    val id: RecipeId,

    val title: String,
    val source: String, // RecipeSource,
    val author: String?,
    val sourceUrl: String?,
    val yield: String?,
    val notes: String?,
    val ingredients: List<Ingredient>?, // This probably does not really want to be nullable...
    val steps: List<String>?,
    @Serializable(with = InstantIso8601Serializer::class) val createdInSource: Instant?,
    @Serializable(with = InstantIso8601Serializer::class) val updatedInSource: Instant?,
    val tags: Set<Tag>,
    val embedding: List<Float>? = null,
    @Serializable(with = InstantIso8601Serializer::class) val verified: Instant? = null,  // null == never verified
) {
    fun toEmbeddingText(): String {
        val tagText = "Tags: ${tags.joinToString(", ")}"
        val ingredientText =
            ingredients?.joinToString("\n") { "- ${it.amount ?: ""} ${it.unit ?: ""} ${it.ingredient} (${it.note ?: ""})".trim() }
                ?: ""
        val stepsText = steps?.joinToString("\n") { "- $it" } ?: ""
        return listOfNotNull(
            tagText,
            "Title: $title",
            "Source: $source",
            author?.let { "Author: $it" },
            sourceUrl?.let { "URL: $it" },
            yield?.let { "Yield: $it" },
            notes?.let { "Notes: $it" },
            "Ingredients:\n$ingredientText",
            "Steps:\n$stepsText"
        ).joinToString("\n\n")
    }

}

@Serializable
data class Ingredient(
    val ingredient: String, val amount: String?, val unit: String?, val note: String?
)
