package com.jacagen.jrecipe

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jacagen.jrecipe.model.Recipe
import jrecipe.composeapp.generated.resources.Res
import jrecipe.composeapp.generated.resources.cat
import kotlinx.browser.window
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource

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
            RecipeList()
        }
    }
}


@Composable
fun RecipeList() {
    var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }

    LaunchedEffect(Unit) {
        val apiBaseUrl = getConfig()["apiBaseUrl"] ?: error("Missing apiBaseUrl in config.json")
        window.fetch("$apiBaseUrl/recipes").then { response ->
            if (response.ok) {
                val text = response.text()
                text.then { textResponse ->
                    recipes = Json.decodeFromString(
                        ListSerializer(Recipe.serializer()),
                        textResponse.toString())
                    null
                }

            } else {
                error("Could not fetch recipes")
            }
            null
        }
    }

    LazyColumn {
        items(recipes) { recipe ->
            Text(
                text = recipe.title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp)
            )
        }
    }
}

private suspend fun getConfig(): Map<String, String> =  // Need to do this properly
    mapOf("apiBaseUrl" to "http://localhost:8080")