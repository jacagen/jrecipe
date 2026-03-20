@file:OptIn(ExperimentalTime::class, ExperimentalAtomicApi::class) @file:JvmName("RestaurantToolsKt")

package com.jacagen.jrecipe.llm.tool

import com.jacagen.jrecipe.data.dao.mongodb.evernoteNoteDao
import com.jacagen.jrecipe.data.dao.mongodb.recipeDao
import com.jacagen.jrecipe.importer.embedAndStore
import com.jacagen.jrecipe.llm.TurnState
import com.jacagen.jrecipe.llm.embeddingModel
import com.jacagen.jrecipe.model.*
import dev.langchain4j.agent.tool.Tool
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.math.sqrt
import kotlin.time.ExperimentalTime

private val logger = LoggerFactory.getLogger(RecipeTools::class.java)

@Suppress("unused")
class RecipeTools(val turnState: TurnState) {
    @Tool
    fun getAllRecipes(): List<RecipeSummary> = runBlocking { recipeDao.getSummariesSortedByTitle() }

    @Tool
    fun getAllTags() = runBlocking {
        recipeDao.getAll().map { it.tags }.flatten().toSet()
    }

    @OptIn(ExperimentalTime::class)
    @Tool
    fun findTopRecipeMatches(query: String, topK: Int = 5): List<Recipe> = runBlocking {
        logger.debug("findTopRecipeMatches: Getting embeddings for query: $query")
        val queryEmbedding = embeddingModel.embed(query).content().vector().toList()

        // This could be streamed better, or more done in  Mongo
        logger.debug("findTopRecipeMatches: Getting recipes")
        val recipes = recipeDao.getAll().filter { it.embedding != null }

        logger.debug("findTopRecipeMatches: Getting cosine similarities")
        val recipesWithSimilarities = recipes.map { cosineSimilarity(it.embedding!!, queryEmbedding) to it }

        logger.debug("findTopRecipeMatches: Sorting results")
        val sortedRecipes = recipesWithSimilarities.sortedByDescending { it.first }
        val result = sortedRecipes.take(topK).map { it.second }.map { it.copy(embedding = null) }
        if (result.size == 1)
            turnState.selectedRecipe.compareAndSet(null, result[0].id)
        logger.debug("findTopRecipeMatches: FindTopRecipeMatches returns {} recipes", result.size)
        result
    }

    @Tool
    fun chooseRandomRecipe(): Recipe = runBlocking {
        val recipe = recipeDao.chooseRandomRecipe()
        turnState.selectedRecipe.compareAndSet(null, recipe.id)
        recipe
    }

    @Tool
    fun importRecipe(id: String, tags: Set<Tag>, recipe: Recipe) = runBlocking {
        logger.debug("importRecipe {}", id)
        @Suppress("UNCHECKED_CAST") val adjustedTags =
            tags.map { TagCatalog[it] }.filter { it != null }.toSet() as Set<Tag>
        val recipeToSave = recipe.copy(id = id, tags = adjustedTags)
        recipeDao.insert(recipeToSave)
        turnState.selectedRecipe.compareAndSet(null, id)
    }

    @Tool
    fun getRecipeById(id: RecipeId) = runBlocking {
        logger.debug("getRecipeById {}", id)
        val result: Recipe = recipeDao.findById(id)!!
        logger.debug("getRecipeById found {}", id)
        turnState.selectedRecipe.compareAndSet(null, id)
        result
    }

    @Tool
    fun updateRecipeContents(id: RecipeId, updatedRecipe: Recipe) = runBlocking {
        logger.debug("updateRecipeContents Update recipe {} with contents: {}", id, updatedRecipe)
        val oldRecipe = recipeDao.findById(id)!!
        val updatedRecipe = updatedRecipe.copy(
            id = id,
            tags = oldRecipe.tags,
        )
        embedAndStore(updatedRecipe)
        turnState.selectedRecipe.compareAndSet(null, id)
    }

    @Tool("Note that a single specific recipe was discussed")
    fun noteChosenRecipe(id: RecipeId) {
        logger.debug("noteChosenRecipe {}", id)
        turnState.selectedRecipe.compareAndSet(null, id)
    }

    @Tool("List raw Evernote recipes")
    fun listRawEvernoteRecipes(): List<EvernoteNoteSummary> = runBlocking {
        logger.debug("Listing all raw Evernote recipes")
        evernoteNoteDao.getSummaries()
    }
}

private fun cosineSimilarity(vecA: List<Float>, vecB: List<Float>): Float {
    val dot = vecA.zip(vecB).sumOf { (a, b) -> (a * b).toDouble() }
    val normA = sqrt(vecA.sumOf { (it * it).toDouble() })
    val normB = sqrt(vecB.sumOf { (it * it).toDouble() })
    return if (normA == 0.0 || normB == 0.0) 0f else (dot / (normA * normB)).toFloat()
}