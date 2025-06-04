package com.jacagen.jrecipe.importer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.jacagen.jrecipe.importer.evernote.dropRecipes
import com.jacagen.jrecipe.importer.evernote.loadEvernoteToMongo
import com.jacagen.jrecipe.importer.evernote.loadLlmRecipesToRecipes
import com.jacagen.jrecipe.importer.evernote.parseEvernoteRecipesWithLlm
import kotlinx.coroutines.runBlocking


class RecipeTool : CliktCommand() {
    val load by option("--load-evernote", help = "Load Evernote notes").flag()
    val dropRecipes by option("--drop-recipes", help = "Drop recipes").flag()
    val parseEvernoteRecipesWithLlm by option("--parse-evernote-recipes-with-llm", help = "Parse recipes with LLM").flag()
    val parseNotesRecipesWithLlmRecipe by option("--parse-notes-recipes-with-llm", help = "Parse recipes with LLM").flag()
    val llmToReal by option("--llm-to-real-recipes", help = "Convert notes").flag()

    override fun run() = runBlocking {
        if (load) {
            println("Loading...")
            loadEvernoteToMongo()
        }
        if (parseEvernoteRecipesWithLlm) {
            println("Parsing Evernote recipes with LLM...")
            parseEvernoteRecipesWithLlm()
        }
        if (parseNotesRecipesWithLlmRecipe) {
            println("Parsing Notes recipes with LLM...")
            parseNotesRecipesWithLlm()
        }
        if (dropRecipes) {
            println("Dropping recipes...")
            dropRecipes()
        }
        if (llmToReal) {
            println("Converting LLM recipes to real recipes...")
            loadLlmRecipesToRecipes()
        }
    }
}

fun main(args: Array<String>) = RecipeTool().main(args)