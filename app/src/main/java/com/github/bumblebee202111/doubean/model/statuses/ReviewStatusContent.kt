package com.github.bumblebee202111.doubean.model.statuses

import com.github.bumblebee202111.doubean.model.SimpleUser
import java.time.LocalDateTime

data class ReviewStatusContent(
    val title: String,
    val abstract: String,
    override val createTime: LocalDateTime,
    val author: SimpleUser,
    val subjectLabel: SubjectLabel?,
    override val likeCount: Int,
    override val commentCount: Int,
    override val repostCount: Int,
) : UserStatus {
    override val user: SimpleUser
        get() = author
}

data class SubjectLabel(
    val icon: String,
    val uri: String,
    val title: String,
)

data class ReviewCard(
    val subjectLabel: SubjectLabel,
    val abstract: String,
    val authorUri: String,
    val authorName: String,
    val title: String,
) : StatusCardData
