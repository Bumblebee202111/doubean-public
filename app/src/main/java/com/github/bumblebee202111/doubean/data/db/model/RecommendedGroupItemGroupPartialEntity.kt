package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo

data class RecommendedGroupItemGroupPartialEntity(
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
    @ColumnInfo("avatar_url")
    val avatarUrl: String,
    @ColumnInfo("member_name")
    val memberName: String,
    @ColumnInfo("desc_abstract")
    val descAbstract: String?,
    val color: Int?,
)