package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.groups.PinnedTabItem
import java.util.Calendar

data class PopulatedPinnedTabItem(
    @ColumnInfo("pinned_date")
    val pinnedDate: Calendar,
    @ColumnInfo("group_id")
    val groupId: String,
    @ColumnInfo("group_name")
    val groupName: String?,
    @ColumnInfo("group_avatar")
    val groupAvatar: String?,
    @ColumnInfo("tab_id")
    val tabId: String,
    @ColumnInfo("tab_name")
    val tabName: String,
)

fun PopulatedPinnedTabItem.toPinnedTabItem() = PinnedTabItem(
    pinnedDate = pinnedDate,
    groupId = groupId,
    groupName = groupName,
    groupAvatar = groupAvatar,
    tabId = tabId,
    tabName = tabName
)