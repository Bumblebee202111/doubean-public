package com.doubean.ford.data.db.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.doubean.ford.model.PostItem

data class PopulatedPostItem(
    @Embedded
    val partialEntity: PostItemPartialEntity,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "id"
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

    //@Relation(...)
    //val viewedPost
)

fun PopulatedPostItem.asExternalModel() = PostItem(
    id = partialEntity.id,
    title = partialEntity.title,
    author = author.asExternalModel(),
    created = partialEntity.created,
    lastUpdated = partialEntity.lastUpdated,
    commentCount = partialEntity.commentCount,
    tags = postTags.map(GroupPostTagEntity::asExternalModel),
    coverUrl = partialEntity.coverUrl,
    url = partialEntity.url,
    uri = partialEntity.uri
    //hasBeenView = ...,
    //isBlocked = ...
)
