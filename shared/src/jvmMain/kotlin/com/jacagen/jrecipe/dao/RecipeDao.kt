package com.jacagen.jrecipe.dao

import com.jacagen.jrecipe.model.Recipe
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface RecipeDao {
    suspend fun getAll(): List<Recipe>
    suspend fun getAllSortedByTitle(): List<Recipe>
    suspend fun findById(id: String): Recipe?
    suspend fun insert(recipe: Recipe)
    suspend fun insert(recipes: List<Recipe>)
    suspend fun update(recipe: Recipe)
    suspend fun delete(id: Uuid)
    suspend fun deleteAll()
}