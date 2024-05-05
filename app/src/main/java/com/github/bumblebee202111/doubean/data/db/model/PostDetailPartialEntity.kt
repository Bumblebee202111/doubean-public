package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.SizedImage
import java.time.LocalDateTime

data class PostDetailPartialEntity(

    val id: String,

    val title: String,

    @ColumnInfo("author_id")
    val authorId: String,

    val created: LocalDateTime? = null,

    @ColumnInfo("last_updated")
    val lastUpdated: LocalDateTime? = null,

    @ColumnInfo("like_count")
    val likeCount: Int? = null,

    @ColumnInfo("reaction_count")
    val reactionCount: Int? = null,

    @ColumnInfo("repost_count")
    val repostCount: Int? = null,

    @ColumnInfo("save_count")
    val saveCount: Int? = null,

    @ColumnInfo("comment_count")
    val commentCount: Int? = null,

    @ColumnInfo("short_content")
    val shortContent: String? = null,

    val content: String?,

    @ColumnInfo("cover_url")
    val coverUrl: String? = null,

    val url: String,

    val uri: String,
    @ColumnInfo("group_id")
    val groupId: String,

    val images: List<SizedImage>? = null,

    @ColumnInfo("ip_location")
    val ipLocation: String? = null,
)
