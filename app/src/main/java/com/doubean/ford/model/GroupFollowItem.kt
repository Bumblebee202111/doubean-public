package com.doubean.ford.model

import java.util.*

// TODO: convert to sealed class, allow items of a search, saved posts and user posts ...
data class GroupFollowItem(
    val followDate: Calendar,
    val groupId: String,
    val groupName: String?,
    val groupAvatarUrl: String?,
    val tabId: String? = null,
    val groupTabName: String? = null,
)