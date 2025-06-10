package com.jacagen.jrecipe.service

import dev.langchain4j.service.UserMessage

interface RecipeChatBot {
    fun chat(@UserMessage input: String): String     // Later add streamining?
}