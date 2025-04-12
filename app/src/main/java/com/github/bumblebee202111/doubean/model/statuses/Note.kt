package com.github.bumblebee202111.doubean.model.statuses

import com.github.bumblebee202111.doubean.model.SimpleUser
import com.github.bumblebee202111.doubean.model.SizedImage
import java.time.LocalDateTime

data class Note(
    val title: String,
    val abstract: String,
    val author: SimpleUser,
    val imageUrls: List<SizedImage>,
    override val createTime: LocalDateTime,
    override val likeCount: Int,
    override val commentCount: Int,
    override val repostCount: Int,
) : UserStatus {
    override val user: SimpleUser
        get() = author
}