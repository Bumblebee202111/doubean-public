package com.github.bumblebee202111.doubean.feature.groups.groupdetail

import com.github.bumblebee202111.doubean.model.group.GroupDetail
import com.github.bumblebee202111.doubean.model.group.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.group.SimpleGroupWithColor
import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class GroupDetailUiState(
    val cachedGroup: SimpleGroupWithColor? = null,
    val group: GroupDetail? = null,
    val notificationPreferences: GroupNotificationPreferences? = null,
    val isLoading: Boolean = false,
    val errorMessage: UiMessage? = null,
)
