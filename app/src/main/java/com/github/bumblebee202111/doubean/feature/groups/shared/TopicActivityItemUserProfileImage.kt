package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.ui.component.UserProfileImage

@Composable
fun TopicActivityItemUserProfileImage(url: String?, onClick: () -> Unit) {
    UserProfileImage(
        url = url,
        size = 24.dp,
        onClick = onClick
    )
}