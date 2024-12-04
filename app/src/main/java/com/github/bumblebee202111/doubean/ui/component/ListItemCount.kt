package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ListItemCount(iconVector: ImageVector, value: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(iconVector, contentDescription = null)
        Text(text = value.toString(), style = MaterialTheme.typography.labelLarge)
    }
}
