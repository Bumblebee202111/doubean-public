package com.github.bumblebee202111.doubean.model.statuses

import com.github.bumblebee202111.doubean.model.SizedImage
import com.github.bumblebee202111.doubean.model.fangorns.User
import com.github.bumblebee202111.doubean.model.subjects.Rating
import java.time.LocalDateTime


sealed interface UserStatus {
    val createTime: LocalDateTime
    val user: User?
    val likeCount: Int
    val commentCount: Int
    val resharesCount: Int

}

data class UserStatusTypeUnsupported(
    override val createTime: LocalDateTime,
    override val user: User?,
    override val likeCount: Int,
    override val commentCount: Int,
    override val resharesCount: Int,
    val type: String,
) :
    UserStatus

data class UserCardStatus(
    override val user: User,
    val action: String?,
    val rating: Rating?,
    val body: String?,
    val card: StatusCardData, 
    override val createTime: LocalDateTime,
    override val likeCount: Int,
    override val commentCount: Int,
    override val resharesCount: Int,
) : UserStatus

data class UserRepostStatus(
    val author: User,
    val body: String? = null,
    val reposted: UserStatus,
    override val createTime: LocalDateTime,
    override val likeCount: Int,
    override val commentCount: Int,
    override val resharesCount: Int,
) : UserStatus {
    override val user: User
        get() = author
}

data class UserSimpleStatus(
    val author: User,
    override val createTime: LocalDateTime,
    val body: String?,
    val images: List<SizedImage>?,
    override val likeCount: Int,
    override val commentCount: Int,
    override val resharesCount: Int,
) : UserStatus {
    override val user: User
        get() = author
}

data class UserTopicRepostStatus(
    val author: User,
    val body: String?,
    val topic: Topic,
    override val createTime: LocalDateTime,
    override val likeCount: Int,
    override val commentCount: Int,
    override val resharesCount: Int,
) : UserStatus {
    override val user: User
        get() = author
}

