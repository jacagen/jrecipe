package com.jacagen.jrecipe.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jacagen.jrecipe.model.Recipe

@Composable
fun RowScope.NavigationColumn(recipes: List<Recipe>, onSelect: (Recipe) -> Unit) {
    val grouped = mapOf(
        "Recipes" to recipes.filter { true }, // Placeholder - adjust if categorization is needed
        "Ingredients" to emptyList(), // Future implementation
        "Other" to emptyList()
    )

    LazyColumn(
        modifier = Modifier.Companion
            .weight(1f)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        grouped.forEach { (sectionTitle, sectionRecipes) ->

        }

        // Tags
        item {
            var expanded by remember { mutableStateOf(true) }
            Column(modifier = Modifier.Companion.fillMaxWidth()) {
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    Text(
                        text = "Tags",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.Companion.weight(1f)
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
        }

        // Recipes
        item {
            var expanded by remember { mutableStateOf(true) }
            Column(modifier = Modifier.Companion.fillMaxWidth()) {
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    Text(
                        text = "Recipes",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.Companion.weight(1f)
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }

                if (expanded) {
                    recipes.forEach { recipe ->
                        Row(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable { onSelect(recipe) },
                            verticalAlignment = Alignment.Companion.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalDining,
                                contentDescription = "Recipe"
                            )
                            Text(
                                text = recipe.title,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.Companion.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Ingredients
        item {
            var expanded by remember { mutableStateOf(true) }
            Column(modifier = Modifier.Companion.fillMaxWidth()) {
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.Companion.weight(1f)
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
        }





    }
}