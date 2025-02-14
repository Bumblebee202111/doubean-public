package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.TopicSortBy

data class GroupGroupNotificationTargetPartialEntity(
    @ColumnInfo("group_id")
    val groupId: String,
    @ColumnInfo("notifications_enabled")
    val notificationsEnabled: Boolean,
    @ColumnInfo("sort_by")
    val sortBy: TopicSortBy,
    @ColumnInfo("max_topics_per_fetch")
    val maxTopicsPerFetch: Int,
    @ColumnInfo("notify_on_updates")
    val notifyOnUpdates: Boolean,
)
