package com.jacagen.jrecipe.component

import js.core.JsAny
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import mui.icons.material.HourglassEmpty
import mui.icons.material.Send
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import org.w3c.fetch.*
import react.*
import react.dom.html.ReactHTML.div
import react.dom.onChange
import web.cssom.px
import web.html.HTMLInputElement

external interface ChatColumnProps : Props

val ChatColumn = FC<ChatColumnProps> {
    val coroutineScope = useMemo { MainScope() }

    var userInput by useState("")
    var messages by useState(listOf("Welcome to the LLM chat!"))
    var isThinking by useState(false)

    Box {
        Typography {
            variant = TypographyVariant.h6
            sx { padding = 8.px }
            +"Chat"
        }

        Box {
            messages.forEach { message ->
                div {
                    ReactMarkdown {
                        this.children = message
                    }
                }
            }
        }

        Box {
            TextField {
                value = userInput
                onChange = { event ->
                    val target = event.target as? HTMLInputElement
                    if (target != null) {
                        userInput = target.value
                    }
                }
                label = ReactNode("Ask a question...")
                variant = FormControlVariant.outlined
                fullWidth = true
            }

            IconButton {
                onClick = {
                    if (userInput.isNotBlank()) {
                        val currentInput = userInput
                        userInput = ""
                        messages = messages + "You: $currentInput"

                        coroutineScope.launch {
                            isThinking = true
                            try {
                                val reply = submitChatRequest(createChatRequest(currentInput)).toString()
                                messages = messages + "LLM: $reply"
                            } catch (e: Throwable) {
                                messages = messages + "ERROR: $e"
                            } finally {
                                isThinking = false
                            }
                        }
                    }
                }

                if (isThinking) {
                    HourglassEmpty()
                } else {
                    Send()
                }
            }
        }
    }
}

// Utilities

suspend fun submitChatRequest(requestInit: RequestInit): JsAny {
    val response = window.fetch("http://localhost:8080/chat", requestInit).await()  // Don't hardcode
    if (response.ok) return response.text().await()
    else throw Exception(response.statusText)
}

fun createChatRequest(input: String): RequestInit {
    return RequestInit(
        method = "POST",
        headers = Headers().apply {
            append("Content-Type", "text/plain")
        },
        body = input
    ).apply {
        referrer = ""
        referrerPolicy = "no-referrer"
        mode = RequestMode.CORS
        credentials = RequestCredentials.SAME_ORIGIN
        cache = RequestCache.DEFAULT
        redirect = RequestRedirect.FOLLOW
        integrity = ""
        keepalive = false
    }
}