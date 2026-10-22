package com.github.bumblebee202111.doubean.model.subject

import com.github.bumblebee202111.doubean.model.SizedPhoto
import com.github.bumblebee202111.doubean.model.user.User
import java.time.LocalDateTime


data class ReviewDetail(
    val id: String,
    val title: String,
    val abstract: String,
    val content: String,
    val rtype: String?,
    val typeName: String?,
    val uri: String,
    val url: String,
    val sharingUrl: String,
    val createTime: LocalDateTime,
    val editTime: LocalDateTime?,
    val ipLocation: String?,
    val updatedIpLocation: String?,
    val activity: String?,
    val rating: Rating.NonNull?,
    val user: User,
    val photos: List<SizedPhoto>,
    val subject: ReviewSubject?,
    val usefulCount: Int,
    val uselessCount: Int,
    val reactionsCount: Int,
    val reactionType: Int,
    val commentsCount: Int,
    val resharesCount: Int,
    val collectionsCount: Int,
    val readCount: Int,
    val isCollected: Boolean,
    val spoiler: Boolean,
    val isOriginal: Boolean,
    val isBookJuryReview: Boolean,
    val totalCntForAuthor: Int,
)

data class ReviewSubject(
    val id: String,
    val title: String,
    val type: SubjectType,
    val rawType: String,
    val uri: String,
    val url: String,
    val coverUrl: String,
    val cardSubtitle: String,
)