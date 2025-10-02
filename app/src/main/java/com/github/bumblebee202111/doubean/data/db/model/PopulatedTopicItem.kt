package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.bumblebee202111.doubean.model.groups.TopicItem

data class PopulatedTopicItem(
    @Embedded
    val partialEntity: TopicItemPartialEntity,
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

    
    
)

fun PopulatedTopicItem.asExternalModel() = TopicItem(
    id = partialEntity.id,
    title = partialEntity.title,
    author = author.toUser(),
    createTime = partialEntity.createTime,
    updateTime = partialEntity.updateTime,
    commentsCount = partialEntity.commentsCount,
    tags = topicTags.map(GroupTopicTagEntity::asExternalModel),
    coverUrl = partialEntity.coverUrl,
    url = partialEntity.url,
    uri = partialEntity.uri
    
    
)
