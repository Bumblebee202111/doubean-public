package com.github.bumblebee202111.doubean.model.common

import com.github.bumblebee202111.doubean.model.Photo
import com.github.bumblebee202111.doubean.model.fangorns.User
import com.github.bumblebee202111.doubean.model.statuses.SubjectStatusCard
import com.github.bumblebee202111.doubean.model.statuses.UserStatus
import com.github.bumblebee202111.doubean.model.subjects.Rating
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest

sealed interface FeedContent {
    val type: String
}

data class TopicFeedContent(
    override val type: String,
    val uri: String,
    val alt: String,
    val title: String,
    val abstractString: String,
    val photos: List<Photo>,
    val photosCount: Int,
    val author: User,
) : FeedContent

data class SubjectFeedContent(
    override val type: String,
    val subject: SubjectWithInterest<*>,
) : FeedContent

data class ReviewFeedContent(
    override val type: String,
    val id: String,
    val uri: String,
    val alt: String,
    val author: User,
    val rating: Rating?,
    val title: String,
    val abstractString: String,
    val photos: List<Photo>,
    val card: SubjectStatusCard<*>,
) : FeedContent

data class StatusFeedContent(
    override val type: String,
    val status: UserStatus,
) : FeedContent

data class NoteFeedContent(
    override val type: String,
    val author: User,
    val title: String,
    val abstract: String,
    val photos: List<Photo>,
) : FeedContent

data class UnknownFeedContent(override val type: String) : FeedContent