package com.jacagen.jrecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.w3c.fetch.*

@Composable
fun RowScope.ChatColumn() {
    val coroutineScope = rememberCoroutineScope()
    var userInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf("Welcome to the LLM chat!")) }
    var isThinking by remember { mutableStateOf(false) }

    // Scroll state for chat messages
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.Companion.weight(2f).fillMaxHeight().padding(8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = "Chat", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.Companion.padding(8.dp)
        )
        Column(
            modifier = Modifier.Companion.weight(1f).verticalScroll(scrollState).padding(8.dp)
        ) {
            // Scroll to bottom whenever messages change -- this needs a lot of improvement
            LaunchedEffect(messages) {
                snapshotFlow { scrollState.maxValue }.first().let { max -> scrollState.animateScrollTo(max) }
            }
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
        IconButton(
            onClick = {
                if (userInput.isNotBlank()) {
                    messages = messages + "You: $userInput"
                    val input = userInput.toJsString()
                    userInput = ""
                    val requestInit = createChatRequest(input)
                    coroutineScope.launch {
                        isThinking = true   // Also should make the button non-clickable
                        try {
                            val reply: JsAny = submitChatRequest(requestInit)
                            messages = messages + "LLM: $reply"
                        } catch (e: Throwable) {
                            messages = messages + "ERROR: [Error fetching response] $e"
                        } finally {
                            isThinking = false
                        }
                    }
                }
            }, modifier = Modifier.Companion.align(Alignment.Companion.End).padding(top = 8.dp)
        ) {
            if (isThinking) Icon(Icons.Filled.HourglassEmpty, contentDescription = "Waiting...")
            else Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
        }
    }
}

private suspend fun submitChatRequest(requestInit: RequestInit): JsAny {
    val apiBaseUrl = getConfig()["apiBaseUrl"] ?: error("Missing apiBaseUrl in config.json")
    val responseWaiter = window.fetch("$apiBaseUrl/chat", requestInit)
    val responseObject: JsAny = responseWaiter.await()
    val response = responseObject as Response
    val reply: JsAny = response.text().await()
    return reply
}

private fun createChatRequest(input: JsString): RequestInit {
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
    return requestInit
}