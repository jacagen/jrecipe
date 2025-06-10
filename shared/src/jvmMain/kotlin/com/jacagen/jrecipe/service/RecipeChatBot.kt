package com.jacagen.jrecipe.service

import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.service.UserMessage

interface RecipeChatBot {
    @UserMessage("{{it}}")
    fun chat(message: String): String     // Later add streamining?
}

class RecipeHelper {
    @Tool
    fun getAllTags() = setOf("Breakfast", "Drink")
}