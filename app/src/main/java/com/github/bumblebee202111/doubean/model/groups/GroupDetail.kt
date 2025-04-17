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
    val avatar: String,
    val memberName: String?,
    val createTime: LocalDateTime?,
    val description: String?,
    val tabs: List<GroupTab>,
    val color: String?,
    val memberRole: GroupMemberRole?,
    val isSubscriptionEnabled: Boolean?,
    val isSubscribed: Boolean?,
) {
    fun findTab(tabId: String?): GroupTab? {
        Log.d("xxx", tabs.firstOrNull { tab -> tab.id == tabId }.toString())
        return tabs.firstOrNull { tab -> tab.id == tabId }
    }
}

data class GroupTab(
    val id: String,
    val name: String?,
    val seq: Int,
)

fun GroupDetail.toSimpleGroup() = SimpleGroup(
    id = id,
    name = name,
    url = url,
    uri = uri,
    avatar = avatar
)
