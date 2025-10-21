package com.github.bumblebee202111.doubean.feature.groups.groupdetail

import com.github.bumblebee202111.doubean.model.groups.GroupDetail
import com.github.bumblebee202111.doubean.model.groups.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.groups.SimpleGroupWithColor

data class GroupDetailUiState(
    val cachedGroup: SimpleGroupWithColor? = null,
    val groupDetail: GroupDetail? = null,
    val notificationPreferences: GroupNotificationPreferences? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)
