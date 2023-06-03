package com.doubean.ford.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.doubean.ford.model.PostComment
import com.doubean.ford.model.RepliedToPostComment

data class PopulatedPostComment(
    @Embedded
    val entity: PostCommentEntity,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "id"
    )
    val author: UserEntity,
    @Relation(
        entity = PostCommentEntity::class,
        parentColumn = "replied_to_id",
        entityColumn = "id"
    )
    val repliedTo: PopulatedRepliedToPostComment?,
)

data class PopulatedRepliedToPostComment(
    @Embedded
    val entity: PostCommentEntity,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "id"
    )
    val author: UserEntity,
)

fun PopulatedPostComment.asExternalModel() = PostComment(
    id = entity.id,
    author = author.asExternalModel(),
    photos = entity.photos,
    text = entity.text,
    created = entity.created,
    voteCount = entity.voteCount,
    ipLocation = entity.ipLocation,
    repliedTo = repliedTo?.asExternalModel()
)

fun PopulatedRepliedToPostComment.asExternalModel() = RepliedToPostComment(
    id = entity.id,
    author = author.asExternalModel(),
    photos = entity.photos,
    text = entity.text,
    created = entity.created,
    voteCount = entity.voteCount,
    ipLocation = entity.ipLocation,
)
