package com.jacagen.jrecipe.dao.mongodb

import com.jacagen.jrecipe.dao.RecipeDao
import com.jacagen.jrecipe.model.Recipe
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MongoRecipeDao(private val collection: MongoCollection<Recipe>) : RecipeDao {

    override suspend fun getAll(): List<Recipe> {
        return collection.find().toList()
    }

    override suspend fun getAllSortedByTitle(): List<Recipe> {
        return collection.find().sort(Document("title", 1)).toList()

    }

    override suspend fun findById(id: Uuid): Recipe? {
        val filter = org.bson.Document("_id", id.toString())
        return collection.find(filter).firstOrNull()
    }

    override suspend fun insert(recipe: Recipe) {
        collection.insertOne(recipe)
    }

    override suspend fun update(recipe: Recipe) {
        val filter = org.bson.Document("_id", recipe.id.toString())
        collection.replaceOne(filter, recipe)
    }

    override suspend fun delete(id: Uuid) {
        val filter = org.bson.Document("_id", id.toString())
        collection.deleteOne(filter)
    }

    override suspend fun insert(recipes: List<Recipe>) {
        collection.insertMany(recipes)
    }

    override suspend fun deleteAll() {
        collection.deleteMany(Document())
    }
}