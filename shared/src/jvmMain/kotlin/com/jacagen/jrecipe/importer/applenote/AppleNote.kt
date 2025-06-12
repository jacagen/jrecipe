@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.importer.applenote

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Serializable
data class AppleNoteRecipe(
    val _id: String,
    val title: String,
    val tags: List<String>,
    val sourceUrl: String? = null,
    val ingredients: List<Ingredient>,
    val tools: List<String>? = emptyList(),
    val steps: List<String>,
    val prepTimeMinutes: Int? = null,
    val cookTimeMinutes: Int? = null,
    val calories: Int? = null,
    val yield: String? = null,
    val notes: String? = null
)

@Serializable
data class Ingredient(
    val name: String,
    val amount: String?,
    val notes: String? = null,
    val optional: Boolean? = false
)