@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.importer

import com.jacagen.jrecipe.data.dao.mongodb.recipeDao
import kotlin.uuid.ExperimentalUuidApi

internal suspend fun recipeExists(id: String) =
    recipeDao.findById(id) != null