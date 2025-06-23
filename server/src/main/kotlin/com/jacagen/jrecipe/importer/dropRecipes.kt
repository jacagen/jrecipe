package com.jacagen.jrecipe.importer

import com.jacagen.jrecipe.data.dao.mongodb.recipeDao

internal suspend fun dropRecipes() = recipeDao.deleteAll()
