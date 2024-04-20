package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import java.time.LocalDateTime

data class GroupDetailPartialEntity(
    val id: String,

    val name: String,

    @ColumnInfo("member_count")
    val memberCount: Int,

    @ColumnInfo("post_count")
    val postCount: Int,

    @ColumnInfo("share_url")
    val shareUrl: String,

    val url: String,

    @ColumnInfo("avatar_url")
    val avatarUrl: String,

    @ColumnInfo("date_created")
    val dateCreated: LocalDateTime?,

    @ColumnInfo("member_name")
    val memberName: String,

    val description: String?,

    val color: Int?,

    val uri: String,
)