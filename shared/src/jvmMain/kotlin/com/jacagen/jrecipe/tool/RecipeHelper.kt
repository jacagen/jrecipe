package com.jacagen.jrecipe.tool

import com.jacagen.jrecipe.dao.mongodb.recipeCollection
import com.jacagen.jrecipe.dao.mongodb.recipeDao
import com.jacagen.jrecipe.llm.embeddingModel
import com.jacagen.jrecipe.model.Recipe
import dev.langchain4j.agent.tool.Tool
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime

@Suppress("unused")
class RecipeHelper {
    @Tool
    fun getAllTags() = runBlocking {
        recipeDao.getAll()
            .map { it.tags }
            .flatten()
            .toSet()
    }

    @OptIn(ExperimentalTime::class)
    @Tool
    fun findTopRecipeMatches(query: String, topK: Int = 5): List<Recipe> = runBlocking {
        println("Getting embeddings for query: $query")
        val queryEmbedding = embeddingModel.embed(query).content().vector().toList()

        // This could be streamed better, or more done in  Mongo
        println("Getting recipes")
        val recipes = recipeCollection.find().toList().filter { it.embedding != null }

        println("Getting cosine similarities")
        val recipesWithSimilarities = recipes.map { cosineSimilarity(it.embedding!!, queryEmbedding) to it }

        println("Sorting results")
        val sortedRecipes = recipesWithSimilarities.sortedByDescending { it.first }
        sortedRecipes.take(topK).map { it.second }.map { it.copy(embedding = null) }
    }
}

fun cosineSimilarity(vecA: List<Float>, vecB: List<Float>): Float {
    val dot = vecA.zip(vecB).sumOf { (a, b) -> (a * b).toDouble() }
    val normA = kotlin.math.sqrt(vecA.sumOf { (it * it).toDouble() })
    val normB = kotlin.math.sqrt(vecB.sumOf { (it * it).toDouble() })
    return if (normA == 0.0 || normB == 0.0) 0f else (dot / (normA * normB)).toFloat()
}