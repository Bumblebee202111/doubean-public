package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.bumblebee202111.doubean.model.groups.TopicDetail

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
        entity = CachedGroupEntity::class
    )
    val group: CachedGroupEntity? = null,

    //@Relation(...)
    //val viewedTopic
)


fun PopulatedTopicDetail.toTopicDetail() = TopicDetail(
    id = partialEntity.id,
    title = partialEntity.title,
    author = author.asExternalModel(),
    createTime = partialEntity.createTime,
    updateTime = partialEntity.updateTime,
    editTime = partialEntity.editTime,
    likeCount = partialEntity.likeCount,
    reactionCount = partialEntity.reactionCount,
    isCollected = partialEntity.isCollected,
    resharesCount = partialEntity.resharesCount,
    collectionsCount = partialEntity.collectionsCount,
    commentCount = partialEntity.commentsCount,
    shortContent = partialEntity.shortContent,
    content = partialEntity.content,
    tags = topicTags.map(GroupTopicTagEntity::asExternalModel),
    coverUrl = partialEntity.coverUrl,
    reactionType = partialEntity.reactionType,
    url = partialEntity.url,
    group = group?.toSimpleGroupWithColor(),
    uri = partialEntity.uri,
    images = partialEntity.images,
    ipLocation = partialEntity.ipLocation
)
