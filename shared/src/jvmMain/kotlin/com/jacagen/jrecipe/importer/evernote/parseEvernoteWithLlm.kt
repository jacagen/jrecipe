@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.jacagen.jrecipe.importer.evernote

import com.jacagen.jrecipe.dao.mongodb.database
import com.jacagen.jrecipe.dao.mongodb.recipeCollection
import com.jacagen.jrecipe.importer.recipeExists
import com.jacagen.jrecipe.llm.model
import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.service.EvernoteToLlmConverter
import dev.langchain4j.service.AiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi


private const val cooksIllustrated = "Cooks Illustrated"

private val assistant = AiServices.builder(EvernoteToLlmConverter::class.java)
    .chatModel(model)
    .build()

internal suspend fun parseEvernoteRecipesWithLlm() {
    val evernoteCollection = database.getCollection<EvernoteNote>("evernote")
    evernoteCollection.find().filter { !recipeExists(it.id) }.collect { evernote ->
        delay(5000) // Someday add more graceful rate limiting (see https://chatgpt.com/share/683f2059-1c48-8003-bcfc-59a8795d5785)
        try {
            val recipe = evernote.toRecipe()
            recipeCollection.insertOne(recipe)
        } catch (x: Throwable) {
            println("ERROR: Could not save recipe ${evernote.title}")
            x.printStackTrace()
        }
    }
}

private suspend fun EvernoteNote.toRecipe() = withContext(Dispatchers.IO) {
    val recipe = assistant.convertRecipe(this@toRecipe)

    // For some reason the LLM doesn't do a good job of preserving the id.  Fix this and adjust
    // other stuff
    val adjustedId = id
    val adjustedTitle = recipe.adjustTitle()
    val adjustedAuthor = recipe.adjustAuthor()
    val adjustedRecipe = recipe.copy(id = adjustedId, title = adjustedTitle, author = adjustedAuthor)

    println("Processed recipe $title")
    adjustedRecipe
}

private fun Recipe.adjustTitle() = title.removeSuffix(" - Cooks Illustrated")
private fun Recipe.adjustAuthor() =
    if (title.endsWith(" - Cooks Illustrated")) {
        when (author) {
            "Cooks Illustrated" -> cooksIllustrated
            "Cook's Illustrated" -> cooksIllustrated
            null -> "Cooks Illustrated"
            else -> error("Conflicting authors $this")
        }
    } else author

