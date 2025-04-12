package com.github.bumblebee202111.doubean.model.statuses

import com.github.bumblebee202111.doubean.model.SimpleUser
import com.github.bumblebee202111.doubean.model.SizedImage
import com.github.bumblebee202111.doubean.model.subjects.Rating
import java.time.LocalDateTime


sealed interface UserStatus {
    val createTime: LocalDateTime
    val user: SimpleUser
    val likeCount: Int
    val commentCount: Int
    val repostCount: Int

}

data class UserStatusTypeUnsupported(
    override val createTime: LocalDateTime,
    override val user: SimpleUser,
    override val likeCount: Int,
    override val commentCount: Int,
    override val repostCount: Int,
    val type: String,
) :
    UserStatus

data class UserCardStatus(
    override val user: SimpleUser,
    val action: String?,
    val rating: Rating?,
    val body: String?,
    val card: StatusCardData, 
    override val createTime: LocalDateTime,
    override val likeCount: Int,
    override val commentCount: Int,
    override val repostCount: Int,
) : UserStatus

data class UserRepostStatus(
    val author: SimpleUser,
    val body: String? = null,
    val reposted: UserStatus,
    override val createTime: LocalDateTime,
    override val likeCount: Int,
    override val commentCount: Int,
    override val repostCount: Int,
) : UserStatus {
    override val user: SimpleUser
        get() = author
}

data class UserSimpleStatus(
    val author: SimpleUser,
    override val createTime: LocalDateTime,
    val body: String?,
    val images: List<SizedImage>?,
    override val likeCount: Int,
    override val commentCount: Int,
    override val repostCount: Int,
) : UserStatus {
    override val user: SimpleUser
        get() = author
}

data class UserTopicRepostStatus(
    val author: SimpleUser,
    val body: String?,
    val topic: Topic,
    override val createTime: LocalDateTime,
    override val likeCount: Int,
    override val commentCount: Int,
    override val repostCount: Int,
) : UserStatus {
    override val user: SimpleUser
        get() = author
}

