package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo

data class JoinedGroupPartialEntity(
    val id: String,
    val name: String,

    @ColumnInfo("member_count")
    val memberCount: Int,

    @ColumnInfo("topic_count")
    val topicCount: Int,

    @ColumnInfo("share_url")
    val shareUrl: String,

    val url: String,

    val uri: String,

    val avatar: String,

    @ColumnInfo("member_name")
    val memberName: String,

    @ColumnInfo("desc_abstract")
    val descAbstract: String?,

    val color: String,
)