package com.jacagen.jrecipe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val recipes = remember {
                    listOf(
                        "Chocolate Cake" to "Flour, eggs, cocoa, sugar",
                        "Pasta Primavera" to "Pasta, vegetables, garlic, olive oil",
                        "Avocado Toast" to "Bread, avocado, salt, lemon"
                    )
                }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    recipes.forEach { (title, content) ->
                        Column(Modifier.fillMaxWidth().safeContentPadding()) {
                            Text(title, style = MaterialTheme.typography.headlineSmall)
                            Text(content, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}