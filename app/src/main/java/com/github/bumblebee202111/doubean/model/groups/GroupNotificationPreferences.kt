package com.github.bumblebee202111.doubean.model.groups

import com.github.bumblebee202111.doubean.data.db.model.GroupGroupNotificationTargetPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabNotificationTargetPartialEntity

data class GroupNotificationPreferences(
    val notificationsEnabled: Boolean,
    val sortBy: TopicSortBy,
    val maxTopicNotificationsPerFetch: Int,
    val notifyOnUpdates: Boolean,
)

fun GroupNotificationPreferences.toGroupNotificationTargetPartialEntity(
    groupId: String,
): GroupGroupNotificationTargetPartialEntity {
    return GroupGroupNotificationTargetPartialEntity(
        groupId = groupId,
        notificationsEnabled = notificationsEnabled,
        sortBy = sortBy,
        maxTopicNotificationsPerFetch = maxTopicNotificationsPerFetch,
        notifyOnUpdates = notifyOnUpdates
    )
}

fun GroupNotificationPreferences.toGroupTabNotificationTargetPartialEntity(
    groupId: String,
    tabId: String,
): GroupTabNotificationTargetPartialEntity {
    return GroupTabNotificationTargetPartialEntity(
        groupId = groupId,
        tabId = tabId,
        notificationsEnabled = notificationsEnabled,
        sortBy = sortBy,
        maxTopicNotificationsPerFetch = maxTopicNotificationsPerFetch,
        notifyOnUpdates = notifyOnUpdates
    )
}