package com.ixsvf.ixcafe.screens.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun HorizontalSpace(width: Int) {
    Spacer(modifier = Modifier.width(width = width.dp))
}

@Composable
fun VerticalSpace(height: Int) {
    Spacer(modifier = Modifier.height(height = height.dp))
}