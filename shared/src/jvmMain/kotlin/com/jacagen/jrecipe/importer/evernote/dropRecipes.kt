package com.jacagen.jrecipe.importer.evernote

import com.jacagen.jrecipe.dao.mongodb.recipeDao

internal suspend fun dropRecipes() = recipeDao.deleteAll()
