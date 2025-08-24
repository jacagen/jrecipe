@file:OptIn(ExperimentalTime::class) @file:JvmName("RestaurantToolsKt")

package com.jacagen.jrecipe.llm.tool

import com.jacagen.jrecipe.data.dao.mongodb.recipeDao
import com.jacagen.jrecipe.importer.embedAndStore
import com.jacagen.jrecipe.llm.embeddingModel
import com.jacagen.jrecipe.model.*
import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import kotlinx.coroutines.runBlocking
import kotlin.math.sqrt
import kotlin.time.ExperimentalTime

@Suppress("unused")
class RecipeTools {
    @Tool
    fun getAllRecipes(): List<RecipeSummary> = runBlocking { recipeDao.getSummariesSortedByTitle() }

    @Tool
    fun getAllTags() = runBlocking {
        recipeDao.getAll().map { it.tags }.flatten().toSet()
    }

    @OptIn(ExperimentalTime::class)
    @Tool
    fun findTopRecipeMatches(query: String, topK: Int = 5): List<Recipe> = runBlocking {
        println("Getting embeddings for query: $query")
        val queryEmbedding = embeddingModel.embed(query).content().vector().toList()

        // This could be streamed better, or more done in  Mongo
        println("Getting recipes")
        val recipes = recipeDao.getAll().filter { it.embedding != null }

        println("Getting cosine similarities")
        val recipesWithSimilarities = recipes.map { cosineSimilarity(it.embedding!!, queryEmbedding) to it }

        println("Sorting results")
        val sortedRecipes = recipesWithSimilarities.sortedByDescending { it.first }
        sortedRecipes.take(topK).map { it.second }.map { it.copy(embedding = null) }
    }

    @Tool
    fun importRecipe(id: String, tags: Set<Tag>, recipe: Recipe) = runBlocking {
        @Suppress("UNCHECKED_CAST") val adjustedTags =
            tags.map { TagCatalog[it] }.filter { it != null }.toSet() as Set<Tag>
        val recipeToSave = recipe.copy(id = id, tags = adjustedTags)
        recipeDao.insert(recipeToSave)
    }

    @Tool
    fun getRecipeById(id: RecipeId) = runBlocking {
        recipeDao.findById(id)
    }

    @Tool
    fun updateRecipeContents(id: RecipeId, updatedRecipe: Recipe) = runBlocking{
        println("**** Update recipe $id with contents: $updatedRecipe")
        val oldRecipe = recipeDao.findById(id)!!
        val updatedRecipe = updatedRecipe.copy(
            id = id,
            tags = oldRecipe.tags,
        )
        embedAndStore(updatedRecipe)
    }

    @Tool("Forward a recipe to the UI")
    fun forwardRecipeToUi(
        @P("Recipe ID (UUID or slug)") id: RecipeId
    ): RecipeId {
        //onSelect(id)              // <-- your side effect to update the other part of the app
        println("Forward recipe $id")
        return "Forward recipe $id"
    }
}

fun cosineSimilarity(vecA: List<Float>, vecB: List<Float>): Float {
    val dot = vecA.zip(vecB).sumOf { (a, b) -> (a * b).toDouble() }
    val normA = sqrt(vecA.sumOf { (it * it).toDouble() })
    val normB = sqrt(vecB.sumOf { (it * it).toDouble() })
    return if (normA == 0.0 || normB == 0.0) 0f else (dot / (normA * normB)).toFloat()
}