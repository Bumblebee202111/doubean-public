package com.github.bumblebee202111.doubean.data.db.model

import com.github.bumblebee202111.doubean.model.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.TopicSortBy

sealed interface GroupNotificationTarget {
    val groupId: String
    val notificationsEnabled: Boolean
    val sortBy: TopicSortBy
    val maxTopicsPerFetch: Int
    val notifyOnUpdates: Boolean
    val lastFetchedTimeMillis: Long
}

fun GroupNotificationTarget.toGroupNotificationPreferences() = GroupNotificationPreferences(
    notificationsEnabled = notificationsEnabled,
    sortBy = sortBy,
    maxTopicsPerFetch = maxTopicsPerFetch,
    notifyOnUpdates = notifyOnUpdates,
)