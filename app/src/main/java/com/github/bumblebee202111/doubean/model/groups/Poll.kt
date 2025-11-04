package com.github.bumblebee202111.doubean.model.groups

import kotlinx.serialization.Serializable

data class Poll(
    val expireTime: String?,
    val inputType: String,
    val id: String,
    val title: String,
    val votedUserCount: Int,
    val votedLimit: Int,
    val options: List<Option>,
) : TopicContentEntity {
    @Serializable
    data class Option(
        val id: String,
        val isVoted: Boolean,
        var pollId: String,
        val title: String,
        val userId: String,
        val votedUserCount: Int,
    )
}
