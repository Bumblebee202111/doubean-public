package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.time.LocalDateTime

data class PostGroupPartialEntity(
    @PrimaryKey
    val id: String,

    val name: String,

    @ColumnInfo("member_count")
    val memberCount: Int = 0,

    @ColumnInfo("post_count")
    val postCount: Int = 0,

    @ColumnInfo("date_created")
    val dateCreated: LocalDateTime? = null,

    val url: String? = null,

    @ColumnInfo("avatar_url")
    val avatarUrl: String? = null,

    @ColumnInfo("member_name")
    val memberName: String? = null,

    @ColumnInfo("short_description")
    val shortDescription: String? = null,

    val color: Int?,
)

