package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.ui.component.UserProfileImage

@Composable
fun TopicActivityItemUserProfileImage(url: String?, onClick: () -> Unit) {
    UserProfileImage(
        url = url,
        size = dimensionResource(id = R.dimen.icon_size_small),
        onClick = onClick
    )
}