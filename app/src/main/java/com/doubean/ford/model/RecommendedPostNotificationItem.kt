package com.doubean.ford.model

import java.time.LocalDateTime

data class RecommendedPostNotificationItem(
    val id: String,

    val title: String,

    val author: User,

    val created: LocalDateTime,

    val lastUpdated: LocalDateTime,

    val commentCount: Int,

    val tags: List<GroupPostTag>,

    val coverUrl: String?,

    val url: String,

    val uri: String,
    val group: PostItemGroup,

    ) {
    val tag: GroupPostTag?
        get() = tags.firstOrNull()
}
