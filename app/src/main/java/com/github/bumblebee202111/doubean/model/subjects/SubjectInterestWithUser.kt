package com.github.bumblebee202111.doubean.model.subjects

import com.github.bumblebee202111.doubean.model.fangorns.User
import java.time.LocalDateTime

data class SubjectInterestWithUser(
    val comment: String?,
    val rating: Rating.NonNull?,
    val status: SubjectInterestStatus,
    val voteCount: Int,
    val createTime: LocalDateTime,
    val user: User,
)

