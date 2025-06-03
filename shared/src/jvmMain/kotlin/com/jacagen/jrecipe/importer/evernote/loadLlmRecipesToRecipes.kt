@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.jacagen.jrecipe.importer.evernote

import com.fasterxml.jackson.core.type.TypeReference
import com.jacagen.jrecipe.dao.mongodb.database
import com.jacagen.jrecipe.dao.mongodb.recipeDao
import com.jacagen.jrecipe.llm.model
import com.jacagen.jrecipe.llm.objectMapper
import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.model.RecipeIngredient
import com.mongodb.client.model.Filters.eq
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.kotlin.model.chat.chat
import dev.langchain4j.model.chat.request.ChatRequest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private suspend fun recipeExists(id: Uuid) =
    recipeDao.findById(id) != null

internal suspend fun loadLlmRecipesToRecipes() {
    llmRecipeCollection.find()
        .filter { !recipeExists(it._id) }
        .map { llmRecipe ->
            llmRecipe.toRecipe()
        }.collect { recipeDao.insert(it) }
}


private fun LlmRecipe.toRecipe() =
    Recipe(
        id = _id,
        title = adjustTitle(),
        source = source,
        author = adjustAuthor(),
        sourceUrl = sourceUrl,
        ingredients = ingredients ?: emptyList(),
        steps = steps ?: emptyList(),
        createdInSource = createdInSource,
        updatedInSource = updatedInSource,
        tags = tags,
    )

private fun LlmRecipe.adjustTitle() = title.removeSuffix(" - Cook's Illustrated")

private fun LlmRecipe.adjustAuthor() =
    if (title.endsWith(" - Cook's Illustrated")) {
        if (author == "Cook's Illustrated") author
        else if (author == null) "Cook's Illustrated"
        else error("Conflicting authors $this")
    } else author

