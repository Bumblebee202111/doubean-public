package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage

@Composable
fun UserProfileImage(url: String?, size: Dp) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
    )
}