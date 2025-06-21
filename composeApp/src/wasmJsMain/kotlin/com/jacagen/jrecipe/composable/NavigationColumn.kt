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
import androidx.compose.material.icons.automirrored.filled.Label
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
import com.jacagen.jrecipe.model.TagDefinition

@Composable
fun RowScope.NavigationColumn(recipes: List<Recipe>, tags: List<TagDefinition>, onRecipeSelect: (Recipe) -> Unit) {
    LazyColumn(
        modifier = Modifier.Companion
            .weight(1f)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        // Tags
        item {
            var expanded by remember { mutableStateOf(false) }
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

            if (expanded) {
                tags.forEach { tag ->
                    Row(
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Label,
                            contentDescription = "Label"
                        )
                        Text(
                            text = tag.tag,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.Companion.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        // Recipes
        item {
            var expanded by remember { mutableStateOf(false) }
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
                                .clickable { onRecipeSelect(recipe) },
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