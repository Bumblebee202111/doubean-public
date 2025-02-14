package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.bumblebee202111.doubean.model.TopicSortBy

@Entity("group_notification_group_targets")
data class GroupGroupNotificationTargetEntity(
    @PrimaryKey
    @ColumnInfo("group_id")
    override val groupId: String,
    @ColumnInfo("notifications_enabled")
    override val notificationsEnabled: Boolean,
    @ColumnInfo("sort_by")
    override val sortBy: TopicSortBy,
    @ColumnInfo("max_topics_per_fetch")
    override val maxTopicsPerFetch: Int,
    @ColumnInfo("notify_on_updates")
    override val notifyOnUpdates: Boolean,
    @ColumnInfo("last_fetched_time_millis", defaultValue = "0")
    override val lastFetchedTimeMillis: Long,
) : GroupNotificationTarget

