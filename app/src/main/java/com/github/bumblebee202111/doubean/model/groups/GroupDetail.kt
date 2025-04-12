package com.github.bumblebee202111.doubean.model.groups

import android.util.Log
import java.time.LocalDateTime

data class GroupDetail(
    val id: String,
    val name: String,
    val memberCount: Int?,
    val topicCount: Int?,
    val shareUrl: String?,
    val url: String,
    val uri: String,
    val avatarUrl: String,
    val memberName: String?,
    val dateCreated: LocalDateTime?,
    val description: String?,
    val tabs: List<GroupTab>?,
    val color: String?,
    val memberRole: GroupMemberRole?,
    val isSubscriptionEnabled: Boolean?,
    val isSubscribed: Boolean?,
    val isFavorited: Boolean,
    val notificationPreferences: GroupNotificationPreferences?,
) {
    fun findTab(tabId: String?): GroupTab? {
        Log.d("xxx", tabs?.firstOrNull { tab -> tab.id == tabId }.toString())
        return tabs?.firstOrNull { tab -> tab.id == tabId }
    }
}

data class GroupTab(
    val id: String,
    val name: String?,
    val seq: Int,
    val isFavorite: Boolean,
    val notificationPreferences: GroupNotificationPreferences?,
)

fun GroupDetail.toItem() = GroupItem(
    id = id,
    name = name,
    avatarUrl = avatarUrl
)
