@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.importer

import com.jacagen.jrecipe.dao.mongodb.recipeDao
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal suspend fun recipeExists(id: String) =
    recipeDao.findById(id) != null