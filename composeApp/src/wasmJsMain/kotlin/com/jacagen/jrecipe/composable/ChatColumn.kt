package com.jacagen.jrecipe.composable

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
//        Column {
//            Text("The quick brown fox", style = TextStyle(fontFamily = latinFontFamily()))
//            Text("こんにちは世界", style = TextStyle(fontFamily = japaneseFontFamily()))
//        }
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
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Ask a question...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            IconButton(
                onClick = {
                    if (userInput.isNotBlank()) {
                        messages = messages + "You: $userInput"
                        val input = userInput.toJsString()
                        userInput = ""
                        val requestInit = createChatRequest(input)
                        coroutineScope.launch {
                            isThinking = true
                            try {
                                val reply: JsAny = submitChatRequest(requestInit)
                                messages = messages + "LLM: $reply"
                            } catch (e: Throwable) {
                                messages = messages + "ERROR: $e"
                            } finally {
                                isThinking = false
                            }
                        }
                    }
                }) {
                if (isThinking) Icon(Icons.Filled.HourglassEmpty, contentDescription = "Waiting...")
                else Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

private class ChatError(val msg: String) : Exception(msg) {
    override fun toString() = msg
}

private suspend fun submitChatRequest(requestInit: RequestInit): JsAny {
    val apiBaseUrl = getConfig()["apiBaseUrl"] ?: error("Missing apiBaseUrl in config.json")
    val responseWaiter = window.fetch("$apiBaseUrl/chat", requestInit)
    val responseObject: JsAny = responseWaiter.await()
    val response = responseObject as Response
    if (response.ok)
        return response.text().await()
    else
        //if (response.ok) return reply else throw ChatError(reply.toString())
        throw ChatError(response.statusText)
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