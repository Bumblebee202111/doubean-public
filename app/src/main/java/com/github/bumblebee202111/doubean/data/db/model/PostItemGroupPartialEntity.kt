package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.PostItemGroup
import java.time.LocalDateTime

class PostItemGroupPartialEntity(
    val id: String,
    val name: String,

    @ColumnInfo("member_count")
    val memberCount: Int? = null,

    @ColumnInfo("post_count")
    val postCount: Int? = null,

    @ColumnInfo("date_created")
    val dateCreated: LocalDateTime? = null,

    @ColumnInfo("share_url")
    val shareUrl: String,

    val url: String,

    val uri: String,

    @ColumnInfo("avatar_url")
    val avatarUrl: String? = null,

    @ColumnInfo("member_name")
    val memberName: String? = null,

    @ColumnInfo("short_description")
    val shortDescription: String? = null,

    val description: String? = null,

    val color: Int? = null,
)

fun PostItemGroupPartialEntity.asExternalModel() = PostItemGroup(
    id,
    name,
    memberCount,
    postCount,
    dateCreated,
    shareUrl,
    url,
    uri,
    avatarUrl,
    memberName,
    shortDescription,
    description,
    color
)