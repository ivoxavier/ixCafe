package com.ixsvf.ixcafe.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PinDots(pinLength: Int, maxLength: Int, isError: Boolean) {
    val errorColor = MaterialTheme.colorScheme.error
    // CORRIGIDO: Usa cor do tema
    val defaultColor = MaterialTheme.colorScheme.outline
    val filledColor = MaterialTheme.colorScheme.onBackground

    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        repeat(maxLength) { index ->
            val isFilled = index < pinLength
            val color = if (isError) errorColor else if (isFilled) filledColor else defaultColor

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isFilled) color else Color.Transparent)
                    .border(2.dp, color, CircleShape)
            )
        }
    }
}