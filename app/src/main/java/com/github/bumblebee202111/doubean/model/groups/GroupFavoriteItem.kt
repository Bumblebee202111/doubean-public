package com.github.bumblebee202111.doubean.model.groups

import java.util.Calendar

// TODO: convert to sealed class, allow items of a search, saved posts and user posts ...
data class GroupFavoriteItem(
    val favoriteDate: Calendar,
    val groupId: String,
    val groupName: String?,
    val groupAvatar: String?,
    val tabId: String? = null,
    val groupTabName: String? = null,
)