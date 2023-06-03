package com.doubean.ford.model

import java.time.LocalDateTime

data class GroupDetail(
    val id: String,

    val name: String,

    val memberCount: Int,

    val postCount: Int,

    val shareUrl: String,

    val url: String,

    val uri: String,

    val avatarUrl: String,

    val memberName: String,

    val dateCreated: LocalDateTime? = null,

    val description: String?,

    val tabs: List<GroupTab>?,

    val color: Int?,

    val isFollowed: Boolean,
)

data class GroupTab(
    val id: String,
    val name: String?,
    val seq: Int,
    val isFollowed: Boolean,
)
