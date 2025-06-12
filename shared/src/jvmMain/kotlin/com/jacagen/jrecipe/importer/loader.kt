@file:OptIn(ExperimentalTime::class)

package com.jacagen.jrecipe.importer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.jacagen.jrecipe.dao.mongodb.recipeDao
import com.jacagen.jrecipe.importer.evernote.loadEvernoteToMongo
import com.jacagen.jrecipe.importer.evernote.parseEvernoteRecipesWithLlm
import com.jacagen.jrecipe.model.Tag
import com.jacagen.jrecipe.model.TagCatalog
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime


class RecipeTool : CliktCommand() {
    val load by option("--load-evernote", help = "Load Evernote notes").flag()
    val dropRecipes by option("--drop-recipes", help = "Drop recipes").flag()
    val parseRecipesWithLlm by option("--parse-evernote-recipes-with-llm", help = "Parse recipes with LLM").flag()
    val normalizeTags by option("--normalize-tags", help = "Normlize recipe tags").flag()

    override fun run() = runBlocking {
        if (load) {
            println("Loading...")
            loadEvernoteToMongo()
        }
        if (parseRecipesWithLlm) {
            println("Parsing recipes with LLM...")
            parseEvernoteRecipesWithLlm()
        }
        if (dropRecipes) {
            println("Dropping recipes...")
            dropRecipes()
        }
        if (normalizeTags) {
            recipeDao.getAll().map { r ->
                @Suppress("UNCHECKED_CAST") val adjustedTags = r.tags.map { TagCatalog[it] }.filter { it != null }.toSet() as Set<Tag>
                r.copy(tags = adjustedTags)
            }.forEach { recipeDao.update(it) }
        }
    }
}

fun main(args: Array<String>) = RecipeTool().main(args)