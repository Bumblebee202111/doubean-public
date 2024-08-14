package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.GroupMemberRole
import java.time.LocalDateTime

data class GroupDetailPartialEntity(
    val id: String,
    val name: String,
    @ColumnInfo("member_count")
    val memberCount: Int,
    @ColumnInfo("topic_count")
    val topicCount: Int,
    @ColumnInfo("share_url")
    val shareUrl: String?,
    val url: String,
    @ColumnInfo("avatar_url")
    val avatarUrl: String,
    @ColumnInfo("date_created")
    val dateCreated: LocalDateTime?,
    @ColumnInfo("member_name")
    val memberName: String? = null,
    val description: String?,
    val color: Int?,
    val uri: String,
    @ColumnInfo("member_role")
    val memberRole: GroupMemberRole? = null,
    @ColumnInfo("is_subscription_enabled")
    val isSubscriptionEnabled: Boolean? = null,
    @ColumnInfo("is_subscribed")
    val isSubscribed: Boolean? = null,
)