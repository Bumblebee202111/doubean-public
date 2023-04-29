package com.doubean.ford.data.vo

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * Class officially known as topic/话题
 */
@Entity(tableName = "posts")
data class Post (
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    val title: String,

    val author: User,


    @SerializedName("create_time")
    val created: LocalDateTime? = null,

    @SerializedName("update_time")
    val lastUpdated: LocalDateTime? = null,


    @SerializedName("like_count")
    val likeCount: Int? = null,

    @SerializedName("reactions_count")
    val reactionCount: Int? = null,


    @SerializedName("reshares_count")
    val repostCount: Int? = null,


    @SerializedName("collections_count")
    val saveCount: Int? = null,


    @SerializedName("comments_count")
    val commentCount: Int? = null,

    @SerializedName("abstract")
    val shortContent: String? = null,

    val content: String?,


    @SerializedName("topic_tags")
    val postTags: List<GroupPostTag>? = null,

    @SerializedName("cover_url")
    val coverUrl: String? = null,

    val url: String,

    val group: GroupBrief?=null,

    val uri: String,

    ){
    var groupId: String? = null
    var tagId: String?=null
    val tagName: String?
        get() = if (postTags!!.isEmpty()) null else postTags[0].name
}