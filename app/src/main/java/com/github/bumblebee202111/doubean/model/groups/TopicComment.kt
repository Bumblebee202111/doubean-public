package com.github.bumblebee202111.doubean.model.groups

import com.github.bumblebee202111.doubean.model.SizedPhoto
import com.github.bumblebee202111.doubean.model.User
import java.time.LocalDateTime

data class TopicComment(
    val id: String,
    val author: User,
    val photos: List<SizedPhoto>?,
    val text: String?,
    val createTime: LocalDateTime?,
    val voteCount: Int,
    val ipLocation: String,
    val refComment: TopicRefComment?,
    )

data class TopicRefComment(
    val id: String,
    val author: User,
    val photos: List<SizedPhoto>?,
    val text: String?,
    val createTime: LocalDateTime?,
    val voteCount: Int,
    val ipLocation: String,
)