@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.jacagen.jrecipe.importer

import com.jacagen.jrecipe.dao.mongodb.recipeDao
import com.jacagen.jrecipe.importer.evernote.llmRecipeCollection
import com.jacagen.jrecipe.model.Recipe
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val cooksIllustrated = "Cooks Illustrated"

private suspend fun recipeExists(id: Uuid) =
    recipeDao.findById(id) != null

internal suspend fun loadLlmRecipesToRecipes() {
    llmRecipeCollection.find()
        .filter { recipe ->
            val exists = recipeExists(recipe._id)
            if (exists)
                println("Recipe ${recipe._id} already exists")
            else
                println("Recipe ${recipe._id} does not exist")
            !exists
        }
        .map { it.toRecipe() }
        .collect { recipeDao.insert(it) }
}


private fun LlmRecipe.toRecipe() =
    Recipe(
        id = _id,
        title = adjustTitle(),
        source = source,
        author = adjustAuthor(),
        sourceUrl = sourceUrl,
        yield = yield,
        notes = notes,
        ingredients = ingredients ?: emptyList(),
        steps = steps ?: emptyList(),
        createdInSource = createdInSource,
        updatedInSource = updatedInSource,
        tags = tags,
    )

private fun LlmRecipe.adjustTitle() = title.removeSuffix(" - Cooks Illustrated")

private fun LlmRecipe.adjustAuthor() =
    if (title.endsWith(" - Cooks Illustrated")) {
        when (author) {
            "Cooks Illustrated" -> cooksIllustrated
            "Cook's Illustrated" -> cooksIllustrated
            null -> "Cooks Illustrated"
            else -> error("Conflicting authors $this")
        }
    } else author

