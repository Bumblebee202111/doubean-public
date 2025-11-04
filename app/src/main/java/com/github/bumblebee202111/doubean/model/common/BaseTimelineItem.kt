package com.github.bumblebee202111.doubean.model.common

import com.github.bumblebee202111.doubean.model.fangorns.User
import java.time.LocalDateTime

interface BaseTimelineItem : BaseFeedableItem {
    val createTime: LocalDateTime?
    val owner: User?
    val uid: String
    val actionText: String?
    val reactionsCount: Int
    val commentsCount: Int
    val resharesCount: Int
}
