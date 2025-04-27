package com.github.bumblebee202111.doubean.model.groups

import com.github.bumblebee202111.doubean.model.SizedImage
import com.github.bumblebee202111.doubean.model.User
import com.github.bumblebee202111.doubean.model.fangorns.ReactionType
import java.time.LocalDateTime

data class TopicDetail(
    val id: String,
    val title: String,
    val author: User,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
    val likeCount: Int?,
    val editTime: LocalDateTime?,
    val reactionCount: Int?,
    val isCollected: Boolean?,
    val resharesCount: Int?,
    val collectionsCount: Int?,
    val commentCount: Int?,
    val shortContent: String?,
    val content: String?,
    val tags: List<GroupTopicTag>,
    val coverUrl: String?,
    val reactionType: ReactionType?,
    val url: String,
    val group: SimpleGroupWithColor?,
    val uri: String,
    val images: List<SizedImage>?,
    val ipLocation: String?,
) {
    val tag: GroupTopicTag?
        get() = tags.firstOrNull()
}

