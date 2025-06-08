package com.jacagen.jrecipe.importer

import com.jacagen.jrecipe.dao.mongodb.recipeDao

internal suspend fun dropRecipes() = recipeDao.deleteAll()
