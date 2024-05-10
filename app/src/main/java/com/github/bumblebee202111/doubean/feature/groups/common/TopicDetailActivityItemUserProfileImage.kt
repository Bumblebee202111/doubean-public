package com.github.bumblebee202111.doubean.feature.groups.common

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R

@Composable
fun TopicDetailActivityItemUserProfileImage(url: String?) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier
            .size(dimensionResource(id = R.dimen.icon_size_small))
            .clip(CircleShape)
    )
}