package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.bumblebee202111.doubean.model.PostDetail
import com.github.bumblebee202111.doubean.model.PostGroup

data class PopulatedPostDetail(
    @Embedded
    val partialEntity: PostDetailPartialEntity,
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
    @Relation(
        parentColumn = "group_id",
        entityColumn = "id",
        entity = GroupEntity::class
    )
    val group: PopulatedPostGroup? = null,

    
    
)

data class PopulatedPostGroup(
    @Embedded
    val partialEntity: PostGroupPartialEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val followedGroup: FollowedGroupEntity?,
)


fun PopulatedPostDetail.asExternalModel() = PostDetail(
    id = partialEntity.id,
    title = partialEntity.title,
    author = author.asExternalModel(),
    created = partialEntity.created,
    lastUpdated = partialEntity.lastUpdated,
    likeCount = partialEntity.likeCount,
    reactionCount = partialEntity.reactionCount,
    repostCount = partialEntity.repostCount,
    saveCount = partialEntity.saveCount,
    commentCount = partialEntity.commentCount,
    shortContent = partialEntity.shortContent,
    content = partialEntity.content,
    tags = postTags.map(GroupPostTagEntity::asExternalModel),
    coverUrl = partialEntity.coverUrl,
    url = partialEntity.url,
    group = group?.asExternalModel(),
    uri = partialEntity.uri
)

fun PopulatedPostGroup.asExternalModel() = PostGroup(
    id = partialEntity.id,
    name = partialEntity.name,
    memberCount = partialEntity.memberCount,
    postCount = partialEntity.postCount,
    dateCreated = partialEntity.dateCreated,
    url = partialEntity.url,
    avatarUrl = partialEntity.avatarUrl,
    memberName = partialEntity.memberName,
    shortDescription = partialEntity.shortDescription,
    color = partialEntity.color,
    isFollowed = followedGroup != null
)
