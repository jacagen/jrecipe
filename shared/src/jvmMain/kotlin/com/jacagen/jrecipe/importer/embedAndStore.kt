@file:OptIn(ExperimentalTime::class)

package com.jacagen.jrecipe.importer

import com.jacagen.jrecipe.dao.mongodb.recipeDao
import com.jacagen.jrecipe.llm.embeddingModel
import com.jacagen.jrecipe.model.Recipe
import kotlin.time.ExperimentalTime

suspend fun embedAndStore(recipe: Recipe) {
    val text = recipe.toEmbeddingText()
    val embedding = embeddingModel.embed(text).content()
    val floats = embedding.vector().toList()

    val updated = recipe.copy(embedding = floats)
    recipeDao.update(updated)
}