package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy

data class GroupTabNotificationTargetPartialEntity(
    @ColumnInfo("tab_id")
    val tabId: String,
    @ColumnInfo("group_id")
    val groupId: String,
    @ColumnInfo("notifications_enabled")
    val notificationsEnabled: Boolean,
    @ColumnInfo("sort_by")
    val sortBy: TopicSortBy,
    @ColumnInfo("max_topic_notifications_per_fetch")
    val maxTopicNotificationsPerFetch: Int,
    @ColumnInfo("notify_on_updates")
    val notifyOnUpdates: Boolean,
)
