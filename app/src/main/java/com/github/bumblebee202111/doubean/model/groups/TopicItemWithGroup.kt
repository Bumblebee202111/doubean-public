package com.github.bumblebee202111.doubean.model.groups

import com.github.bumblebee202111.doubean.model.User
import java.time.LocalDateTime

//TODO: delegate
data class TopicItemWithGroup(
    override val id: String,
    override val title: String,
    override val author: User,
    override val createTime: LocalDateTime,
    override val updateTime: LocalDateTime,
    override val commentsCount: Int,
    override val tags: List<GroupTopicTag>,
    override val coverUrl: String?,
    override val url: String,
    override val uri: String,
    val group: GroupItem,
) : AbstractTopicItem

data class TopicItemGroup(
    val id: String,
    val name: String,
    val url: String,
    val uri: String,
    val avatarUrl: String? = null,
)