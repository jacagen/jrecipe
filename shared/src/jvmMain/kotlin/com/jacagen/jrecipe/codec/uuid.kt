@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.codec

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry
import kotlin.jvm.java
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class KotlinUuidCodec : Codec<Uuid> {
    override fun encode(writer: BsonWriter, value: Uuid, encoderContext: EncoderContext) {
        writer.writeString(value.toString())
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext) = Uuid.parse(reader.readString())

    override fun getEncoderClass(): Class<Uuid> = Uuid::class.java
}

class KotlinUuidCodecProvider : CodecProvider {
    override fun <T : Any?> get(clazz: Class<T>, registry: CodecRegistry): Codec<T>? {
        if (clazz == Uuid::class.java) {
            @Suppress("UNCHECKED_CAST")
            return KotlinUuidCodec() as Codec<T>
        }
        return null
    }
}
