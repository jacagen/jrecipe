@file:OptIn(ExperimentalTime::class)

package com.jacagen.jrecipe.codec

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toKotlinInstant
import java.time.Instant as JavaInstant

class KotlinTimeInstantCodec : Codec<Instant> {
    override fun encode(writer: BsonWriter, value: Instant, encoderContext: EncoderContext) {
        writer.writeStartDocument()
        writer.writeInt64("epochSeconds", value.epochSeconds)
        writer.writeInt32("nanosecondsOfSecond", value.nanosecondsOfSecond)
        writer.writeEndDocument()
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): Instant {
        reader.readStartDocument()
        val epochSeconds = reader.readInt64("epochSeconds")
        val nanos = reader.readInt32("nanosecondsOfSecond")
        reader.readEndDocument()

        return JavaInstant.ofEpochSecond(epochSeconds, nanos.toLong()).toKotlinInstant()
    }

    override fun getEncoderClass(): Class<Instant> = Instant::class.java
}

class KotlinTimeInstantCodecProvider : CodecProvider {
    override fun <T : Any?> get(clazz: Class<T>, registry: CodecRegistry): Codec<T>? {
        return if (clazz == Instant::class.java) {
            @Suppress("UNCHECKED_CAST")
            KotlinTimeInstantCodec() as Codec<T>
        } else {
            null    // Fail here?
        }
    }
}