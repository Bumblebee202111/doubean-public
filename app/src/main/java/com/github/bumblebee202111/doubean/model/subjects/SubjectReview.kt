package com.github.bumblebee202111.doubean.model.subjects

import com.github.bumblebee202111.doubean.model.SizedPhoto
import com.github.bumblebee202111.doubean.model.fangorns.User
import java.time.LocalDateTime

data class SubjectReview(
    val rating: Rating?,
    val usefulCount: Int,
    val sharingUrl: String,
    val title: String,
    val url: String,
    val abstract: String,
    val uri: String,
    val photos: List<SizedPhoto>,
    val reactionsCount: Int,
    val commentsCount: Int,
    val user: User,
    val createTime: LocalDateTime,
    var resharesCount: Int,
    val id: String,
    val subjectType: SubjectType,
)