package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.SizedImage
import java.time.LocalDateTime

data class TopicItemPartialEntity(
    val id: String,

    val title: String,

    @ColumnInfo("author_id")
    val authorId: String,

    val created: LocalDateTime,

    @ColumnInfo("last_updated")
    val lastUpdated: LocalDateTime,

    @ColumnInfo("comment_count")
    val commentCount: Int,

    @ColumnInfo("cover_url")
    val coverUrl: String? = null,

    val url: String,

    val uri: String,

    @ColumnInfo("group_id")
    val groupId: String,

    val images: List<SizedImage>?,
)
