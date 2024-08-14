package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.bumblebee202111.doubean.model.GroupMemberRole
import java.time.LocalDateTime

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo("member_count")
    val memberCount: Int? = null,
    @ColumnInfo("topic_count")
    val topicCount: Int? = null,
    @ColumnInfo("share_url")
    val shareUrl: String? = null,
    val url: String,
    @ColumnInfo("date_created")
    val dateCreated: LocalDateTime? = null,
    val uri: String?,
    @ColumnInfo("avatar_url")
    val avatarUrl: String? = null,
    @ColumnInfo("member_name")
    val memberName: String? = null,
    @ColumnInfo("short_description")
    val shortDescription: String? = null,
    val description: String? = null,
    val color: Int? = null,
    @ColumnInfo("member_role")
    val memberRole: GroupMemberRole? = null,
    @ColumnInfo("is_subscription_enabled")
    val isSubscriptionEnabled: Boolean? = null,
    @ColumnInfo("is_subscribed")
    val isSubscribed: Boolean? = null,
)








