package com.jacagen.jrecipe.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalTime::class)
object InstantIso8601Serializer : KSerializer<Instant> {    // It feels like this should maybe not live in `commonMain?
    override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString()) // default ISO-8601 format
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}

@Serializable
data class Recipe @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class) constructor(
    @ObjectId val id: String,

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
