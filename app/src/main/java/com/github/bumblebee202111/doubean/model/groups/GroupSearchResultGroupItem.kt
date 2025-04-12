package com.github.bumblebee202111.doubean.model.groups

import java.time.LocalDateTime



data class GroupSearchResultGroupItem(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val memberName: String?,
    val memberCount: Int?,
    val topicCount: Int?,
    val dateCreated: LocalDateTime?,
    val descAbstract: String?,
    val shareUrl: String,
    val url: String,
    val uri: String,
)