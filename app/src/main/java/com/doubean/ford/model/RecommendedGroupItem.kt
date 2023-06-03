package com.doubean.ford.model

class RecommendedGroupItem(
    val no: Int,
    val group: RecommendedGroupItemGroup,
    //val posts: List<PostItem>
)

class RecommendedGroupItemGroup(
    val id: String,
    val name: String,
    val memberCount: Int,
    val postCount: Int,
    val shareUrl: String,
    val url: String,
    val uri: String?,
    val avatarUrl: String,
    val memberName: String,
    val shortDescription: String?,
    val color: Int?,
    val isFollowed: Boolean,
)
