package com.doubean.ford.data.vo

import androidx.room.Embedded
import androidx.room.Relation

class RecommendedGroup(
    @Embedded
    val recommendedGroupResult: RecommendedGroupResult? = null,
    @Relation(parentColumn = "groupId", entityColumn = "id", entity = Group::class)
    val group: GroupItem,
    @Relation(parentColumn = "postId", entityColumn = "id", entity = Post::class)
    val post: PostItem? = null
)