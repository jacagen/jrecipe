package com.jacagen.jrecipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import jrecipe.composeapp.generated.resources.Res
import jrecipe.composeapp.generated.resources.cat
import org.jetbrains.compose.resources.painterResource


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