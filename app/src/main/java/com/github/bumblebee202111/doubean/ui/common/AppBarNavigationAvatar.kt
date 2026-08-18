package com.github.bumblebee202111.doubean.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.bumblebee202111.doubean.model.fangorns.User

@Composable
fun AppBarNavigationAvatar(
    currentUser: User?,
    onAvatarClick: () -> Unit,
) {
    IconButton(onClick = onAvatarClick) {
        if (currentUser == null) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentUser.avatar)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
        }
    }
}