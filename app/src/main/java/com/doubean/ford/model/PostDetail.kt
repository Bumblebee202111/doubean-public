package com.doubean.ford.model

import java.time.LocalDateTime

data class PostDetail(

    val id: String,

    val title: String,

    val author: User,

    val created: LocalDateTime? = null,

    val lastUpdated: LocalDateTime? = null,

    val likeCount: Int? = null,

    val reactionCount: Int? = null,

    val repostCount: Int? = null,

    val saveCount: Int? = null,

    val commentCount: Int? = null,

    val shortContent: String? = null,

    val content: String?,

    val tags: List<GroupPostTag>,

    val coverUrl: String? = null,

    val url: String,

    val group: PostGroup? = null,

    val uri: String,

    ) {
    val tag: GroupPostTag?
        get() = tags.firstOrNull()
}

data class PostGroup(
    val id: String,

    val name: String,

    val memberCount: Int = 0,

    val postCount: Int = 0,

    val dateCreated: LocalDateTime?,

    val url: String?,

    val avatarUrl: String?,

    val memberName: String?,

    val shortDescription: String?,

    val color: Int?,
    val isFollowed: Boolean,
)

