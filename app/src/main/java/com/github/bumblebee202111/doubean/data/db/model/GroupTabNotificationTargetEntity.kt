package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy

@Entity("group_notification_tab_targets")
data class GroupTabNotificationTargetEntity(
    @PrimaryKey
    @ColumnInfo("tab_id")
    val tabId: String,
    @ColumnInfo("group_id")
    override val groupId: String,
    @ColumnInfo("notifications_enabled")
    override val notificationsEnabled: Boolean,
    @ColumnInfo("sort_by")
    override val sortBy: TopicSortBy,
    @ColumnInfo("max_topic_notifications_per_fetch")
    override val maxTopicNotificationsPerFetch: Int,
    @ColumnInfo("notify_on_updates")
    override val notifyOnUpdates: Boolean,
    @ColumnInfo("last_fetched_time_millis", defaultValue = "0")
    override val lastFetchedTimeMillis: Long,
) : GroupNotificationTarget

