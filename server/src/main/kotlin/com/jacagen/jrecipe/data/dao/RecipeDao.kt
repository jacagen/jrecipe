package com.jacagen.jrecipe.data.dao

import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.model.RecipeSummary
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface RecipeDao {
    suspend fun getAllSortedByTitle(): List<Recipe>
    suspend fun getAll(): List<Recipe>
    suspend fun getSummariesSortedByTitle(): List<RecipeSummary>
    suspend fun chooseRandomRecipe(): Recipe
    suspend fun findById(id: String): Recipe?
    suspend fun insert(recipe: Recipe)
    suspend fun insert(recipes: List<Recipe>)
    suspend fun update(recipe: Recipe)
    suspend fun delete(id: Uuid)
    suspend fun deleteAll()
}