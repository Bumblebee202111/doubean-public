package com.github.bumblebee202111.doubean.model.groups

import com.github.bumblebee202111.doubean.model.SizedImage
import com.github.bumblebee202111.doubean.model.User
import java.time.LocalDateTime

data class TopicDetail(

    val id: String,

    val title: String,

    val author: User,

    val createTime: LocalDateTime? = null,

    val lastUpdated: LocalDateTime? = null,

    val likeCount: Int? = null,

    val reactionCount: Int? = null,

    val resharesCount: Int? = null,

    val saveCount: Int? = null,

    val commentCount: Int? = null,

    val shortContent: String? = null,

    val content: String?,

    val tags: List<GroupTopicTag>,

    val coverUrl: String? = null,

    val url: String,

    val group: TopicGroup? = null,

    val uri: String,

    val images: List<SizedImage>?,

    val ipLocation: String?,
) {
    val tag: GroupTopicTag?
        get() = tags.firstOrNull()
}

data class TopicGroup(
    val id: String,

    val name: String,

    val memberCount: Int = 0,

    val topicCount: Int = 0,

    val dateCreated: LocalDateTime?,

    val url: String?,

    val avatarUrl: String?,

    val memberName: String?,

    val descAbstract: String?,

    val color: String?,

    val isFavorite: Boolean,
)

