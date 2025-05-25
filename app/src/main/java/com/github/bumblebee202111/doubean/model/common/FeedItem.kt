package com.github.bumblebee202111.doubean.model.common

import com.github.bumblebee202111.doubean.model.fangorns.User
import java.time.LocalDateTime

data class FeedItem<T : FeedContent>(
    override val id: String,
    override val uri: String,
    override val alt: String,
    override val type: String,
    override val layout: FeedItemLayout,
    override val createTime: LocalDateTime?,
    override val owner: User?,
    override val uid: String,
    override val actionText: String?,
    override val reactionsCount: Int,
    override val commentsCount: Int,
    override val resharesCount: Int,
    val content: T,
) : BaseTimelineItem {
    override val url: String get() = alt
}

