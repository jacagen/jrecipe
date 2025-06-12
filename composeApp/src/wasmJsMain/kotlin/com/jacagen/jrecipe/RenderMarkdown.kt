package com.jacagen.jrecipe

import androidx.compose.runtime.Composable
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.model.rememberMarkdownState

@Composable
fun RenderMarkdown(content: String) {
    val markdownState = rememberMarkdownState(content)
    Markdown(markdownState)
}