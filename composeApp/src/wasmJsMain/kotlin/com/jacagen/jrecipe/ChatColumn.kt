package com.jacagen.jrecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.fetch.*

@Composable
fun RowScope.ChatColumn() {
    val coroutineScope = rememberCoroutineScope()
    var userInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf("Welcome to the LLM chat!")) }

    Column(
        modifier = Modifier.Companion.weight(2f).fillMaxHeight().padding(8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = "Chat", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.Companion.padding(8.dp)
        )
        Column(
            modifier = Modifier.Companion.weight(1f).verticalScroll(rememberScrollState()).padding(8.dp)
        ) {

            messages.forEach { message ->
                SelectionContainer {
                    RenderMarkdown(message)
                }
            }

        }
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Ask a question...") },
            modifier = Modifier.Companion.fillMaxWidth()
        )
        Button(
            onClick = {
                if (userInput.isNotBlank()) {
                    messages = messages + "You: $userInput"
                    val input = userInput.toJsString()
                    userInput = ""

                    coroutineScope.launch {
                        try {
                            val apiBaseUrl = getConfig()["apiBaseUrl"] ?: error("Missing apiBaseUrl in config.json")
                            val requestInit = RequestInit(
                                method = "POST",
                                headers = Headers().apply {
                                    append(
                                        "Content-Type", "text/plain"
                                    )
                                },  // Or "application/json"
                                body = input,
                                referrer = "",
                                referrerPolicy = "no-referrer".toJsString(),
                                mode = RequestMode.Companion.CORS,
                                credentials = RequestCredentials.Companion.SAME_ORIGIN,
                                cache = RequestCache.Companion.DEFAULT,
                                redirect = RequestRedirect.Companion.FOLLOW,
                                integrity = "", // Default empty means no SRI
                                keepalive = false, // Default is false; only set true for long requests
                            )
                            val responseWaiter = window.fetch("$apiBaseUrl/chat", requestInit)
                            // It would be nice to have some sort of "waiting" icon
                            val responseObject: JsAny = responseWaiter.await()
                            val response = responseObject as Response
                            val reply: JsAny = response.text().await()
                            messages = messages + "LLM: $reply"
                        } catch (e: Throwable) {
                            messages = messages + "ERROR: [Error fetching response] $e"
                        }
                    }

                }
            }, modifier = Modifier.Companion.align(Alignment.Companion.End).padding(top = 8.dp)
        ) {
            Text("Send")
        }
    }
}