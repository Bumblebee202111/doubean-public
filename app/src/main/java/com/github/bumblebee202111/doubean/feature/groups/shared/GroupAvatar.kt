package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.R

@Composable
fun LargeGroupAvatar(avatarUrl: String?) {
    GroupAvatar(
        avatarUrl = avatarUrl,
        size = 56.dp,
        shape = MaterialTheme.shapes.small,
    )
}

@Composable
fun SmallGroupAvatar(avatarUrl: String?) {
    GroupAvatar(
        avatarUrl = avatarUrl,
        size = 16.dp,
        shape = MaterialTheme.shapes.extraSmall,
    )
}

@Composable
private fun GroupAvatar(avatarUrl: String?, size: Dp, shape: Shape) {
    AsyncImage(
        model = avatarUrl,
        contentDescription = stringResource(id = R.string.a11y_group_item_image),
        modifier = Modifier
            .size(size)
            .clip(shape),
        contentScale = ContentScale.Crop
    )
}