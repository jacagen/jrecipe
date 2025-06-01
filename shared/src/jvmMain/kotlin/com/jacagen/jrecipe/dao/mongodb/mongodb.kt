@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.jacagen.jrecipe.dao.mongodb

import com.jacagen.jrecipe.codec.KotlinTimeInstantCodecProvider
import com.jacagen.jrecipe.codec.KotlinUuidCodecProvider
import com.jacagen.jrecipe.model.Recipe
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider

val codecRegistry = CodecRegistries.fromRegistries(
    MongoClientSettings.getDefaultCodecRegistry(),
    CodecRegistries.fromProviders(
        KotlinUuidCodecProvider(),
        KotlinTimeInstantCodecProvider(),
        PojoCodecProvider.builder().automatic(true).build()
    )
)

val settings = MongoClientSettings.builder()
    .codecRegistry(codecRegistry)
    .build()

val coroutineMongoClient = MongoClient.Factory.create(settings) // Should close at app shutdown
val database = coroutineMongoClient.getDatabase("jcmenu")

internal val recipeCollection = database.getCollection<Recipe>("recipe")
val recipeDao = MongoRecipeDao(recipeCollection)