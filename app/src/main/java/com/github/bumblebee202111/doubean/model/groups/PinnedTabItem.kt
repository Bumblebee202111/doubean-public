package com.github.bumblebee202111.doubean.model.groups

import java.util.Calendar

data class PinnedTabItem(
    val pinnedDate: Calendar,
    val groupId: String,
    val groupName: String?,
    val groupAvatar: String?,
    val tabId: String,
    val tabName: String,
)