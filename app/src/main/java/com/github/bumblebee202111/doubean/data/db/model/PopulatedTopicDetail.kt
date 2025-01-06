package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.bumblebee202111.doubean.model.TopicDetail
import com.github.bumblebee202111.doubean.model.TopicGroup

data class PopulatedTopicDetail(
    @Embedded
    val partialEntity: TopicDetailPartialEntity,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "id"
    )
    val author: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = GroupTopicTagEntity::class,
        associateBy = Junction(
            TopicTagCrossRef::class,
            parentColumn = "topic_id",
            entityColumn = "tag_id"
        )
    )
    val topicTags: List<GroupTopicTagEntity>,
    @Relation(
        parentColumn = "group_id",
        entityColumn = "id",
        entity = GroupEntity::class
    )
    val group: PopulatedTopicGroup? = null,

    
    
)

data class PopulatedTopicGroup(
    @Embedded
    val partialEntity: TopicGroupPartialEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val favoriteGroup: FavoriteGroupEntity?,
)


fun PopulatedTopicDetail.asExternalModel() = TopicDetail(
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
    tags = topicTags.map(GroupTopicTagEntity::asExternalModel),
    coverUrl = partialEntity.coverUrl,
    url = partialEntity.url,
    group = group?.asExternalModel(),
    uri = partialEntity.uri,
    images = partialEntity.images,
    ipLocation = partialEntity.ipLocation
)

fun PopulatedTopicGroup.asExternalModel() = TopicGroup(
    id = partialEntity.id,
    name = partialEntity.name,
    memberCount = partialEntity.memberCount,
    topicCount = partialEntity.topicCount,
    dateCreated = partialEntity.dateCreated,
    url = partialEntity.url,
    avatarUrl = partialEntity.avatarUrl,
    memberName = partialEntity.memberName,
    descAbstract = partialEntity.descAbstract,
    color = partialEntity.color,
    isFavorite = favoriteGroup != null
)
