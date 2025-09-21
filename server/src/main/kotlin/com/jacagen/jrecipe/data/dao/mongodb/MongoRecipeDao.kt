package com.jacagen.jrecipe.data.dao.mongodb

import com.jacagen.jrecipe.data.dao.RecipeDao
import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.model.RecipeSummary
import com.mongodb.client.model.Aggregates.sample
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.first
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

    override suspend fun getSummariesSortedByTitle(): List<RecipeSummary> {
        val summary = collection.withDocumentClass(RecipeSummary::class.java)
        return summary.find().projection(Document(mapOf("_id" to 1, "title" to 1, "tags" to 1)))
            .sort(Document("title", 1)).toList()
    }

    override suspend fun chooseRandomRecipe(): Recipe = collection.aggregate<Recipe>(listOf(sample(1))).first()

    override suspend fun findById(id: String): Recipe? {
        val filter = Document("_id", id)
        return collection.find(filter).firstOrNull()
    }

    override suspend fun insert(recipe: Recipe) {
        collection.insertOne(recipe)
    }

    override suspend fun update(recipe: Recipe) {
        val filter = Document("_id", recipe.id)
        collection.replaceOne(filter, recipe)
    }

    override suspend fun delete(id: Uuid) {
        val filter = Document("_id", id.toString())
        collection.deleteOne(filter)
    }

    override suspend fun insert(recipes: List<Recipe>) {
        collection.insertMany(recipes)
    }

    override suspend fun deleteAll() {
        collection.deleteMany(Document())
    }
}