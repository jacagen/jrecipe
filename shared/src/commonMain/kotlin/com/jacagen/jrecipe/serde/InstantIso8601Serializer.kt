package com.jacagen.jrecipe.serde

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object InstantIso8601Serializer : KSerializer<Instant> {    // It feels like this should maybe not live in `commonMain?
    override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString()) // default ISO-8601 format
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.Companion.parse(decoder.decodeString())
    }
}