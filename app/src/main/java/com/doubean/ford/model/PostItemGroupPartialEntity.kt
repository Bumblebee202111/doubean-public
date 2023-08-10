package com.doubean.ford.model

import java.time.LocalDateTime

class PostItemGroup(
    val id: String,
    val name: String,

    val memberCount: Int? = null,

    val postCount: Int? = null,

    val dateCreated: LocalDateTime? = null,

    val shareUrl: String,

    val url: String,

    val uri: String,

    val avatarUrl: String? = null,

    val memberName: String? = null,

    val shortDescription: String? = null,

    val description: String? = null,

    val color: Int? = null,
)