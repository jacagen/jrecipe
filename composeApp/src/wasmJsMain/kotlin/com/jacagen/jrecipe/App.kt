package com.jacagen.jrecipe

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.jacagen.jrecipe.model.Recipe
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.model.rememberMarkdownState
import kotlinx.browser.window
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Suppress("unused")
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
fun RowScope.RecipeListColumn(recipes: List<Recipe>, onSelect: (Recipe) -> Unit) {
    LazyColumn(modifier = Modifier.weight(1f)) {
        items(recipes) { recipe ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onSelect(recipe) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.LocalDining, contentDescription = "Recipe")
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun RowScope.RecipeDetailColumn(recipe: Recipe?) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .weight(3f)
            .verticalScroll(rememberScrollState())
    ) {
        recipe?.let { recipe ->

            // Title
            Title(recipe.title, Icons.Filled.LocalDining, "Recipe")

            // Tags
            RecipeTagRow(recipe.tags!!)
            Spacer(Modifier.height(8.dp))

            // Yield
            if (recipe.yield != null) {
                Header("Yield")
                RenderMarkdown(recipe.yield!!)
                Spacer(Modifier.height(8.dp))
            }

            // Notes
            if (recipe.notes != null) {
                Header("Notes")
                RenderMarkdown(recipe.notes!!)
                Spacer(Modifier.height(8.dp))
            }

            // Ingredients
            if (recipe.ingredients.isNotEmpty()) {
                Header("Ingredients")
                recipe.ingredients.forEach { ingredient ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Kitchen,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        RenderMarkdown(ingredient)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // Steps
            if (recipe.steps.isNotEmpty()) {
                Header("Steps")
                recipe.steps.withIndex().forEach { (index, step) ->
                    Row(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "${index + 1}. ",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        RenderMarkdown(step)
                    }
                }
            }


        } ?: Text("Select a recipe to view details")
    }
}


@Composable
fun RecipeTagRow(tags: Set<String>) {
    if (tags.isNotEmpty()) {
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tags.forEach { tag ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Label,
                        contentDescription = "Tag icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = tag, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}


@Suppress("RedundantSuspendModifier")
private suspend fun getConfig(): Map<String, String> =  // Need to do this properly
    mapOf("apiBaseUrl" to "http://localhost:8080")

@Composable
fun Title(text: String, icon: ImageVector, iconDescription: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconDescription,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        RenderMarkdown("# $text")
        //Text(text, style = MaterialTheme.typography.titleLarge)
    }
    Spacer(Modifier.height(8.dp))
}


@Composable
private fun Header(text: String) {
    RenderMarkdown("## $text")
    //Text(text, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(4.dp))
}

@Composable
fun RenderMarkdown(content: String) {
    val markdownState = rememberMarkdownState(content)
    Markdown(markdownState)
}