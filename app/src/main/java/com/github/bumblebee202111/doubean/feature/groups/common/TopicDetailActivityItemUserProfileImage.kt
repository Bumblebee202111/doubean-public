package com.github.bumblebee202111.doubean.feature.groups.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.ui.common.UserProfileImage

@Composable
fun TopicDetailActivityItemUserProfileImage(url: String?) {
    UserProfileImage(url = url, size = dimensionResource(id = R.dimen.icon_size_small))

}