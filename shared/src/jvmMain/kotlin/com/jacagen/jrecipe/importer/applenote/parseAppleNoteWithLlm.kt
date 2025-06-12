@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.jacagen.jrecipe.importer.applenote

import com.jacagen.jrecipe.dao.mongodb.database
import com.jacagen.jrecipe.dao.mongodb.recipeCollection
import com.jacagen.jrecipe.importer.evernote.EvernoteNote
import com.jacagen.jrecipe.importer.recipeExists
import com.jacagen.jrecipe.llm.model
import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.model.Tag
import com.jacagen.jrecipe.model.TagCatalog
import com.jacagen.jrecipe.service.SourceToReipeConverter
import dev.langchain4j.service.AiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

// Here is the ChatGPT conversation whre I generated the Apple Notes: https://chatgpt.com/share/684ac783-446c-8003-85f5-9185d9a97f5e


private const val cooksIllustrated = "Cooks Illustrated"

private val assistant = AiServices.builder(SourceToReipeConverter::class.java).chatModel(model).build()

internal suspend fun parseAppleNoteRecipesWithLlm() {
    val sourceRecipeCollection = database.getCollection<AppleNoteRecipe>("apple-notes")
    sourceRecipeCollection.find().filter { !recipeExists(it._id) }.collect { appleNote ->
        delay(5000) // Someday add more graceful rate limiting (see https://chatgpt.com/share/683f2059-1c48-8003-bcfc-59a8795d5785)
        try {
            val recipe = appleNote.toRecipe()
            recipeCollection.insertOne(recipe)
        } catch (x: Throwable) {
            println("ERROR: Could not save recipe ${appleNote.title}")
            x.printStackTrace()
        }
    }
}

private suspend fun AppleNoteRecipe.toRecipe() = withContext(Dispatchers.IO) {
    val recipe = assistant.convertRecipe(this@toRecipe)

    // For some reason the LLM doesn't do a good job of preserving the id.  Fix this and adjust
    // other stuff
    val adjustedId = _id

    // Normlize the tags
    @Suppress("UNCHECKED_CAST") val normlizedTags =
        recipe.tags.map { TagCatalog[it] }.filter { it != null }.toSet() as Set<Tag>

    val adjustedRecipe =
        recipe.copy(id = adjustedId, tags = normlizedTags)

    println("Processed recipe $title")
    adjustedRecipe
}



