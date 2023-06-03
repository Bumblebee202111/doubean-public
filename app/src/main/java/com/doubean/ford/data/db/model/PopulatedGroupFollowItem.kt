package com.doubean.ford.data.db.model

import androidx.room.ColumnInfo
import com.doubean.ford.model.GroupFollowItem
import java.util.*

data class PopulatedGroupFollowItem(
    @ColumnInfo("follow_date")
    val followDate: Calendar,
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

fun PopulatedGroupFollowItem.asExternalModel() = GroupFollowItem(
    followDate = followDate,
    groupId = groupId,
    groupName = groupName,
    groupAvatarUrl = groupAvatarUrl,
    tabId = tabId,
    groupTabName = tabName
)