package com.github.bumblebee202111.doubean.model.fangorns

import com.github.bumblebee202111.doubean.model.User
import java.time.LocalDateTime

data class React(
    val time: LocalDateTime,
    val reactionType: ReactionType,
    val user: User,
    val text: String,
)

enum class ReactionType {
    TYPE_CANCEL_VOTE, TYPE_VOTE, TYPE_USELESS
}