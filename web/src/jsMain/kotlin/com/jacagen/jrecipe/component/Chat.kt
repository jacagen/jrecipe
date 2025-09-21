package com.jacagen.jrecipe.component

import com.jacagen.jrecipe.model.ChatResponse
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mui.icons.material.HourglassEmpty
import mui.icons.material.Send
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import org.w3c.files.File
import react.*
import react.dom.html.ReactHTML.div
import react.dom.onChange
import web.cssom.*
import web.html.HTMLInputElement

external interface ChatColumnProps : Props {
    var onSubmitChatRequest: suspend (String, File?) -> ChatResponse
}

val ChatColumn = FC<ChatColumnProps> { props ->
    val coroutineScope = useMemo { MainScope() }

    var userInput by useState("")
    var (messages, setMessages) = useState(listOf("Welcome to the LLM chat!"))
    var isThinking by useState(false)
    var currentFile by useState<File?>(null)

    Box {
        sx {
            height = 100.vh
            display = Display.flex
            flexDirection = FlexDirection.column
            padding = 16.px
        }

        onDragOver = {
            it.preventDefault()
        }
        onDrop = { event ->
            event.preventDefault()
            val file = event.dataTransfer.files.item(0)
            if (file != null) {
                if (file.type == "application/pdf") {
                    currentFile = file as File
                } else {
                    messages = messages + "Cannot handle files of type ${file.type}"
                }
            }
        }

        Typography {
            variant = TypographyVariant.h6
            sx { padding = 8.px }
            +"Chat"
        }

        Box {
            sx {
                flexGrow = number(1.0)
                overflowY = Overflow.scroll
            }
            messages.forEach { message ->
                div {
                    ReactMarkdown {
                        this.children = message
                    }
                }
            }
        }

        Box {
            sx {
                display = Display.flex
                gap = 8.px
                marginTop = 8.px
            }

            TextField {
                sx { flexGrow = number(1.0) }
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
                        setMessages { old -> old + "You: $currentInput" }
                        coroutineScope.launch {
                            isThinking = true
                            try {
                                val (reply, _)  = props.onSubmitChatRequest(currentInput, currentFile)
                                currentFile = null
                                setMessages { old -> old + "LLM: $reply" }
                            } catch (e: Throwable) {
                                setMessages { old -> old + "ERROR: $e" }
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

