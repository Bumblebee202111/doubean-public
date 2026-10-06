package com.github.bumblebee202111.doubean.model.group

data class GroupItemWithIntroInfo(
    val id: String,
    val name: String,
    val url: String,
    val uri: String,
    val avatar: String,
    val memberCount: Int,
    val memberName: String,
    val descAbstract: String?,
)