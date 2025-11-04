package com.github.bumblebee202111.doubean.data.db.model

import com.github.bumblebee202111.doubean.model.groups.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy

sealed interface GroupNotificationTarget {
    val groupId: String
    val notificationsEnabled: Boolean
    val sortBy: TopicSortBy
    val maxTopicNotificationsPerFetch: Int
    val notifyOnUpdates: Boolean
    val lastFetchedTimeMillis: Long
}

fun GroupNotificationTarget.toGroupNotificationPreferences() = GroupNotificationPreferences(
    notificationsEnabled = notificationsEnabled,
    sortBy = sortBy,
    maxTopicNotificationsPerFetch = maxTopicNotificationsPerFetch,
    notifyOnUpdates = notifyOnUpdates,
)