package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.groups.GroupFavoriteItem
import java.util.Calendar

data class PopulatedGroupFavoriteItem(
    @ColumnInfo("favorite_date")
    val favoriteDate: Calendar,
    @ColumnInfo("group_id")
    val groupId: String,
    @ColumnInfo("group_name")
    val groupName: String?,
    @ColumnInfo("group_avatar_url")
    val groupAvatarUrl: String?,
    @ColumnInfo("tab_id")
    val tabId: String? = null,
    @ColumnInfo("tab_name")
    val tabName: String? = null,
)

fun PopulatedGroupFavoriteItem.asExternalModel() = GroupFavoriteItem(
    favoriteDate = favoriteDate,
    groupId = groupId,
    groupName = groupName,
    groupAvatarUrl = groupAvatarUrl,
    tabId = tabId,
    groupTabName = tabName
)