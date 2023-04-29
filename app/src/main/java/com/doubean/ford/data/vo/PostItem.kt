package com.doubean.ford.data.vo

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class PostItem (
    @SerializedName("id")
    override val id: String,
    val title: String,
    val author: User,

    @SerializedName("create_time")
    val created: LocalDateTime,

    @SerializedName("update_time")
    val lastUpdated: LocalDateTime,

    @SerializedName("like_count")
    val likeCount: Int,

    @SerializedName("comments_count")
    val commentCount: Int,

    @SerializedName("topic_tags")
    val postTags: List<GroupPostTag>? = null,
    
    @SerializedName("cover_url")
    val coverUrl: String? = null,
    val url: String,
    val uri: String,
    val group: Group? = null
): Item() {
    lateinit var groupId: String
    var tagId: String? = null
    val tagName: String?
        get() = if (postTags!!.isEmpty()) null else postTags[0].name
}