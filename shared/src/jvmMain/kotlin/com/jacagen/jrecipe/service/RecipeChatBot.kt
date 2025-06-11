package com.jacagen.jrecipe.service

import com.jacagen.jrecipe.dao.mongodb.recipeDao
import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.service.UserMessage
import kotlinx.coroutines.runBlocking

interface RecipeChatBot {
    @UserMessage("{{it}}")
    fun chat(message: String): String     // Later add streamining?
}

class RecipeHelper {
    @Tool
    fun getAllTags() = runBlocking {
        recipeDao.getAll()
            .map { it.tags }
            .flatten()
            .toSet()
    }
}