package com.rama.apps.testapplication.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


/**
 * Created by Dinesh on 27/02/24.
 * dineshkumarcse0060@gmail.com
 */

@Composable
fun StatusMessage(
    messageType: Boolean,
    message: String,
){
    val messageColor = if (messageType) Color.Black else  Color.Red

    Text(
        text = message,
        color = messageColor
    )
}