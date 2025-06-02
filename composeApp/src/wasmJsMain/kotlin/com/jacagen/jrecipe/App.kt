package com.jacagen.jrecipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jacagen.jrecipe.model.Recipe
import kotlinx.browser.window
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@JsName("console")
external object Console {
    fun log(msg: String)
    fun error(msg: String)
    fun warn(msg: String)
    fun info(msg: String)
}

data class Message(val author: String, val body: String)

@Composable
fun App() {
    val isDark = isSystemInDarkTheme()  // or manually toggle via state
    val colors = if (isDark) darkColorScheme() else lightColorScheme()

    MaterialTheme(colorScheme = colors) {
        Surface(modifier = Modifier.fillMaxSize()) {
            RecipeView()
        }
    }
}

@Composable
fun RecipeView() {
    var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    LaunchedEffect(Unit) {
        val apiBaseUrl = getConfig()["apiBaseUrl"] ?: error("Missing apiBaseUrl in config.json")
        window.fetch("$apiBaseUrl/recipes").then { response ->
            if (response.ok) {
                val text = response.text()
                text.then { textResponse ->
                    recipes = Json.decodeFromString(
                        ListSerializer(Recipe.serializer()),
                        textResponse.toString()
                    )
                    null
                }

            } else {
                error("Could not fetch recipes")
            }
            null
        }
    }

    Row(Modifier.fillMaxSize()) {
        RecipeListColumn(recipes = recipes, onSelect = { selectedRecipe = it })
        RecipeDetailColumn(recipe = selectedRecipe)
    }
}

@Composable
fun RecipeListColumn(recipes: List<Recipe>, onSelect: (Recipe) -> Unit) {
    LazyColumn {
        items(recipes) { recipe ->
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp).clickable { onSelect(recipe) }
            )
        }
    }
}

@Composable
fun RowScope.RecipeDetailColumn(recipe: Recipe?) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .weight(1f)
    ) {
        recipe?.let {
            Text("Recipe Details", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Title: ${it.title}", style = MaterialTheme.typography.bodyMedium)
            // Add more fields as needed
        } ?: Text("Select a recipe to view details")
    }
}

private suspend fun getConfig(): Map<String, String> =  // Need to do this properly
    mapOf("apiBaseUrl" to "http://localhost:8080")