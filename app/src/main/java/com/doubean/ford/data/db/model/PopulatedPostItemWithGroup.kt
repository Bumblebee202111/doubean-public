package com.doubean.ford.data.db.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.doubean.ford.model.PostItemWithGroup

data class PopulatedPostItemWithGroup(
    @Embedded
    val partialEntity: PostItemPartialEntity,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "id",
    )
    val author: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = GroupPostTagEntity::class,
        associateBy = Junction(
            PostTagCrossRef::class,
            parentColumn = "post_id",
            entityColumn = "tag_id"
        )
    )
    val postTags: List<GroupPostTagEntity>,

    @Relation(
        parentColumn = "group_id",
        entityColumn = "id",
        entity = GroupEntity::class
    )
    val group: PostItemGroupPartialEntity,
    //@Relation(...)
    //val viewedPost
)

fun PopulatedPostItemWithGroup.asExternalModel() = PostItemWithGroup(
    id = partialEntity.id,
    title = partialEntity.title,
    author = author.asExternalModel(),
    created = partialEntity.created,
    lastUpdated = partialEntity.lastUpdated,
    commentCount = partialEntity.commentCount,
    tags = postTags.map(GroupPostTagEntity::asExternalModel),
    coverUrl = partialEntity.coverUrl,
    url = partialEntity.url,
    uri = partialEntity.uri,
    group = group.asExternalModel()
    //hasBeenView = ...,
    //isBlocked = ...
)
