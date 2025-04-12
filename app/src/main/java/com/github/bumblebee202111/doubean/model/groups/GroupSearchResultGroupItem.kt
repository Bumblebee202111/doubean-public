package com.github.bumblebee202111.doubean.model.groups

import java.time.LocalDateTime

/** Defines the UI layer model of an item of a group list
 * No need to define a separated GroupItem because a group model always comes along with user data in UI layer
 * */

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