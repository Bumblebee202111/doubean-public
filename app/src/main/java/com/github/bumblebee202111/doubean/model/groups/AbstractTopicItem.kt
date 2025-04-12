package com.github.bumblebee202111.doubean.model.groups

import com.github.bumblebee202111.doubean.model.User
import java.time.LocalDateTime

sealed interface AbstractTopicItem {
    val id: String
    val title: String
    val author: User
    val createTime: LocalDateTime
    val updateTime: LocalDateTime
    val commentsCount: Int
    val tags: List<GroupTopicTag>
    val coverUrl: String?
    val url: String
    val uri: String
    val tag: GroupTopicTag?
        get() = tags.firstOrNull()
}