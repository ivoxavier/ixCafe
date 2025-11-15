package com.ixsvf.ixcafe.screens.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color



@Composable
fun LargeTitleText(text: String)
{
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}


@Composable
fun MediumBodyText(text: String)
{
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray
    )
}

