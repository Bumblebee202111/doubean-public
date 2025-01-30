package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import java.time.LocalDateTime

data class RecommendedGroupItemTopicPartialEntity(
    val id: String,

    val title: String,

    @ColumnInfo("author_id")
    val authorId: String,

    @ColumnInfo("create_time")
    val createTime: LocalDateTime,

    @ColumnInfo("update_time")
    val updateTime: LocalDateTime,

    @ColumnInfo("comments_count")
    val commentCount: Int,

    @ColumnInfo("cover_url")
    val coverUrl: String? = null,

    val url: String,

    val uri: String,

    @ColumnInfo("group_id")
    val groupId: String,
)
