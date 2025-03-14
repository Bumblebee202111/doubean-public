package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R

@Composable
fun LargeGroupAvatar(avatarUrl: String?) {
    GroupAvatar(
        avatarUrl = avatarUrl,
        sizeResId = R.dimen.icon_size_extra_large,
        cornerSizeResId = R.dimen.corner_size_small
    )
}

@Composable
fun SmallGroupAvatar(avatarUrl: String?) {
    GroupAvatar(
        avatarUrl = avatarUrl,
        sizeResId = R.dimen.icon_size_extra_small,
        cornerSizeResId = R.dimen.corner_size_extra_small
    )
}

@Composable
private fun GroupAvatar(avatarUrl: String?, sizeResId: Int, cornerSizeResId: Int) {
    AsyncImage(
        model = avatarUrl,
        contentDescription = stringResource(id = R.string.a11y_group_item_image),
        modifier = Modifier
            .size(dimensionResource(id = sizeResId))
            .clip(RoundedCornerShape(dimensionResource(id = cornerSizeResId))),
        contentScale = ContentScale.Crop
    )
}