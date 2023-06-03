package com.doubean.ford.data.db.model

import androidx.room.*
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

    @ColumnInfo("color_string")
    val color: Int? = null,
)