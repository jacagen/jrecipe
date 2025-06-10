package com.jacagen.jrecipe.service

import dev.langchain4j.service.UserMessage

interface RecipeChatBot {
    @UserMessage("{{it}}")
    fun chat(message: String): String     // Later add streamining?
}