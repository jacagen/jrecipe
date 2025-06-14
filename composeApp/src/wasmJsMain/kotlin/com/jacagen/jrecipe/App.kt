package com.jacagen.jrecipe

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.jacagen.jrecipe.model.Ingredient
import com.jacagen.jrecipe.model.Recipe
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

@Composable
fun App() {
    val isDark = isSystemInDarkTheme()  // or manually toggle via state
    val colors = if (isDark) darkColorScheme() else lightColorScheme()

    MaterialTheme(
        colorScheme = colors, typography = Typography(), shapes = Shapes()
    ) {
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
        window.fetch("$apiBaseUrl/recipes?sortByTitle").then { response ->
            if (response.ok) {
                val text = response.text()
                text.then { textResponse ->
                    recipes = Json.decodeFromString(
                        ListSerializer(Recipe.serializer()), textResponse.toString()
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
        ChatColumn()
    }
}

@Composable
fun RowScope.RecipeListColumn(recipes: List<Recipe>, onSelect: (Recipe) -> Unit) {
    LazyColumn(
        modifier = Modifier.weight(1f).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        items(recipes) { recipe ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onSelect(recipe) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.LocalDining, contentDescription = "Recipe")
                Text(
                    text = recipe.title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun RowScope.RecipeDetailColumn(recipe: Recipe?) {
    Column(
        modifier = Modifier.padding(16.dp).weight(3f).verticalScroll(rememberScrollState())
    ) {
        recipe?.let { recipe ->

            // Title
            Title(recipe.title, Icons.Filled.LocalDining, "Recipe")

            // Tags
            RecipeTagRow(recipe.tags)
            SectionSpacer()

            // Yield
            if (recipe.yield != null) {
                Header("Yield")
                RenderMarkdown(recipe.yield!!)
                SectionSpacer()
            }

            // Notes
            if (recipe.notes != null) {
                Header("Notes")
                RenderMarkdown(recipe.notes!!)
                SectionSpacer()
            }

            // Ingredients
            Ingredients(recipe)

            // Steps
            if (recipe.steps?.isNotEmpty() ?: false) {
                Header("Steps")
                recipe.steps!!.withIndex().forEach { (index, step) ->
                    Row(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "${index + 1}. ", style = MaterialTheme.typography.bodyLarge
                        )
                        RenderMarkdown(step)
                    }
                }
            }


        } ?: Text("Select a recipe to view details")
    }
}

@Composable
private fun Ingredients(recipe: Recipe) {
    if (recipe.ingredients?.isNotEmpty() ?: false) {
        Header("Ingredients")
        recipe.ingredients!!.forEach { ingredient ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                Icon(
                    imageVector = Icons.Filled.Kitchen, contentDescription = null, modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                RenderMarkdown(ingredient.format())
            }
            ListSpacer()
        }
        SectionSpacer()
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
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 4.dp).background(
                        color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small
                    ).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Label,   // Fix
                        contentDescription = "Tag icon", modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = tag, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}


@Suppress("RedundantSuspendModifier")
internal suspend fun getConfig(): Map<String, String> =  // Need to do this properly
    mapOf("apiBaseUrl" to "http://localhost:8080")

@Composable
fun Title(text: String, icon: ImageVector, iconDescription: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon, contentDescription = iconDescription, modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(horizontal = 8.dp)
        )
        //RenderMarkdown("# $text")
        //Text(text, style = MaterialTheme.typography.titleLarge)
    }
    Spacer(Modifier.height(8.dp))
}


@Composable
private fun Header(text: String) {
    Text(
        text = text, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(vertical = 4.dp)
    )
}


@Composable
fun SectionSpacer() {
    Spacer(Modifier.height(12.dp))
}

@Composable
fun ListSpacer() {
    Spacer(Modifier.height(4.dp))
}


private fun Ingredient.format() = StringBuilder().apply {
    if (amount != null) append(amount)
    if (unit != null) append(" ").append(unit)
    append(" ").append(ingredient)
    if (note != null) append(", ").append(note)
}.trimStart().toString()