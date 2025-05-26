@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.jacagen.jrecipe.io

import com.jacagen.jrecipe.io.codec.KotlinUuidCodecProvider
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider

val codecRegistry = CodecRegistries.fromRegistries(
    MongoClientSettings.getDefaultCodecRegistry(),
    CodecRegistries.fromProviders(
        KotlinUuidCodecProvider(),
        PojoCodecProvider.builder().automatic(true).build()
    )
)

val settings = MongoClientSettings.builder()
    .codecRegistry(codecRegistry)
    .build()

val coroutineMongoClient = MongoClient.Factory.create(settings) // Should close at app shutdown
val database = coroutineMongoClient.getDatabase("jcmenu")