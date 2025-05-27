package com.jacagen.jrecipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import jrecipe.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import jrecipe.composeapp.generated.resources.cat


data class Message(val author: String, val body: String)

@Composable
fun App() {
    MessageCard(Message("Jamie", "Hello"))
}

@Composable
fun MessageCard(msg: Message) {
    Row {
        Image(
            painter = painterResource(Res.drawable.cat),
            contentDescription = "Profile picture"
        )
        Column {
            Text(text = msg.author)
            Text(text = msg.body)
        }
    }
}