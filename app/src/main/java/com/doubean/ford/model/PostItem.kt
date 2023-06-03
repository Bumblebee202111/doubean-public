package com.doubean.ford.model

import java.time.LocalDateTime

data class PostItem(
    val id: String,

    val title: String,

    val author: User,

    val created: LocalDateTime,

    val lastUpdated: LocalDateTime,

    val commentCount: Int,

    val tags: List<GroupPostTag>,

    val coverUrl: String? = null,
    val url: String,
    val uri: String,
) {
    var tagId: String? = null
    val tag: GroupPostTag?
        get() = tags.firstOrNull()
}