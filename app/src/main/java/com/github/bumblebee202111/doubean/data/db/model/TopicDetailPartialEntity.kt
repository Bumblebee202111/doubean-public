package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.SizedImage
import com.github.bumblebee202111.doubean.model.fangorns.ReactionType
import java.time.LocalDateTime

data class TopicDetailPartialEntity(

    val id: String,

    val title: String,

    @ColumnInfo("author_id")
    val authorId: String,

    @ColumnInfo("create_time")
    val createTime: LocalDateTime,

    @ColumnInfo("update_time")
    val updateTime: LocalDateTime,

    @ColumnInfo("edit_time")
    val editTime: LocalDateTime? = null,

    @ColumnInfo("like_count")
    val likeCount: Int? = null,

    @ColumnInfo("reaction_count")
    val reactionCount: Int? = null,

    @ColumnInfo("is_collected")
    val isCollected: Boolean?,

    @ColumnInfo("reshares_count")
    val resharesCount: Int? = null,

    @ColumnInfo("collections_count")
    val collectionsCount: Int? = null,

    @ColumnInfo("comments_count")
    val commentsCount: Int? = null,

    @ColumnInfo("short_content")
    val shortContent: String? = null,

    val content: String?,

    @ColumnInfo("cover_url")
    val coverUrl: String? = null,

    @ColumnInfo("reaction_type")
    val reactionType: ReactionType? = null,

    val url: String,

    val uri: String,
    @ColumnInfo("group_id")
    val groupId: String,

    val images: List<SizedImage>? = null,

    @ColumnInfo("ip_location")
    val ipLocation: String? = null,
)
