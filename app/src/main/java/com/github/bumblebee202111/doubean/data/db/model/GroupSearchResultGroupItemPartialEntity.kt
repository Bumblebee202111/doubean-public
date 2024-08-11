package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.GroupSearchResultGroupItem
import java.time.LocalDateTime

data class GroupSearchResultGroupItemPartialEntity(

    val id: String,

    val name: String,

    @ColumnInfo("avatar_url")
    val avatarUrl: String? = null,

    @ColumnInfo("member_name")
    val memberName: String? = null,

    @ColumnInfo("member_count")
    val memberCount: Int? = null,

    @ColumnInfo("topic_count")
    val topicCount: Int? = null,

    @ColumnInfo("date_created")
    val dateCreated: LocalDateTime? = null,

    @ColumnInfo("short_description")
    val shortDescription: String? = null,

    @ColumnInfo("share_url")
    val shareUrl: String,

    val url: String,

    val uri: String,
)

fun GroupSearchResultGroupItemPartialEntity.asExternalModel() = GroupSearchResultGroupItem(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    memberName = memberName,
    memberCount = memberCount,
    topicCount = topicCount,
    dateCreated = dateCreated,
    shortDescription = shortDescription,
    shareUrl = shareUrl,
    url = url,
    uri = uri
)

