package com.rama.apps.testapplication.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rama.apps.testapplication.ui.theme.Primary


/**
 * Created by Dinesh on 21/02/24.
 * dineshkumarcse0060@gmail.com
 */

@Composable
fun AppProgressbar(
    showProgress: Boolean
) {
    if (!showProgress) return
    androidx.wear.compose.material.CircularProgressIndicator(
        modifier = Modifier
            .height(50.dp)
            .width(50.dp),
        strokeWidth = 3.dp,
        indicatorColor = Primary,
        )
}

