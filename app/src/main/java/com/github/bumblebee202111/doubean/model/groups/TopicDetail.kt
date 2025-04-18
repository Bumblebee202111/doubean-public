package com.github.bumblebee202111.doubean.model.groups

import com.github.bumblebee202111.doubean.model.SizedImage
import com.github.bumblebee202111.doubean.model.User
import java.time.LocalDateTime

data class TopicDetail(
    val id: String,
    val title: String,
    val author: User,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
    val likeCount: Int? = null,
    val editTime: LocalDateTime?,
    val reactionCount: Int? = null,
    val resharesCount: Int? = null,
    val saveCount: Int? = null,
    val commentCount: Int? = null,
    val shortContent: String? = null,
    val content: String?,
    val tags: List<GroupTopicTag>,
    val coverUrl: String? = null,
    val url: String,
    val group: SimpleGroupWithColor? = null,
    val uri: String,
    val images: List<SizedImage>?,
    val ipLocation: String?,
) {
    val tag: GroupTopicTag?
        get() = tags.firstOrNull()
}

