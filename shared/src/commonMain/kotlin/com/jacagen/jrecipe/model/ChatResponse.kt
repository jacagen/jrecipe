package com.jacagen.jrecipe.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val response: String,
    val selectedRecipe: Recipe? = null,
)