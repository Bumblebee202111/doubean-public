package com.github.bumblebee202111.doubean.model.statuses

import com.github.bumblebee202111.doubean.model.SizedImage
import com.github.bumblebee202111.doubean.model.fangorns.User
import java.time.LocalDateTime

data class Note(
    val title: String,
    val abstract: String,
    val author: User,
    val imageUrls: List<SizedImage>,
    override val createTime: LocalDateTime,
    override val likeCount: Int,
    override val commentCount: Int,
    override val resharesCount: Int,
) : UserStatus {
    override val user: User
        get() = author
}