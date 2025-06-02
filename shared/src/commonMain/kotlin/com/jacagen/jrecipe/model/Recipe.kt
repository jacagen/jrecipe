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
import kotlin.uuid.Uuid

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
    @ObjectId val id: Uuid,

    val title: String,
    val source: String, // RecipeSource,
    val author: String?,
    val sourceUrl: String?,
    val content: String,
    @Serializable(with = InstantIso8601Serializer::class) val createdInSource: Instant,
    @Serializable(with = InstantIso8601Serializer::class) val updatedInSource: Instant,
    val tags: Set<Tag>,
)
