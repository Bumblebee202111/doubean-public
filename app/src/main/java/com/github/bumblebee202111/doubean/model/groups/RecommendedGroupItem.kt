package com.github.bumblebee202111.doubean.model.groups

data class RecommendedGroupItem(
    val no: Int,
    val group: RecommendedGroupItemGroup,
    
)

data class RecommendedGroupItemGroup(
    val id: String,
    val name: String,
    val memberCount: Int,
    val topicCount: Int,
    val shareUrl: String,
    val url: String,
    val uri: String?,
    val avatarUrl: String,
    val memberName: String,
    val descAbstract: String?,
    val color: String?,
    val isFavorite: Boolean,
)
