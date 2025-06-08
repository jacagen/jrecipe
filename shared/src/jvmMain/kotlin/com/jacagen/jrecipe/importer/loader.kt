package com.jacagen.jrecipe.importer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.jacagen.jrecipe.importer.evernote.loadEvernoteToMongo
import com.jacagen.jrecipe.importer.evernote.parseEvernoteRecipesWithLlm
import kotlinx.coroutines.runBlocking


class RecipeTool : CliktCommand() {
    val load by option("--load-evernote", help = "Load Evernote notes").flag()
    val dropRecipes by option("--drop-recipes", help = "Drop recipes").flag()
    val parseRecipesWithLlm by option("--parse-evernote-recipes-with-llm", help = "Parse recipes with LLM").flag()

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
    }
}

fun main(args: Array<String>) = RecipeTool().main(args)