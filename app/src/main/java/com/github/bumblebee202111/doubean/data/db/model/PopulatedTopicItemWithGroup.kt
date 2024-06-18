package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.bumblebee202111.doubean.model.TopicItemWithGroup

data class PopulatedTopicItemWithGroup(
    @Embedded
    val partialEntity: TopicItemPartialEntity,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "id",
    )
    val author: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = GroupTopicTagEntity::class,
        associateBy = Junction(
            PostTagCrossRef::class,
            parentColumn = "post_id",
            entityColumn = "tag_id"
        )
    )
    val postTags: List<GroupTopicTagEntity>,

    @Relation(
        parentColumn = "group_id",
        entityColumn = "id",
        entity = GroupEntity::class
    )
    val group: TopicItemGroupPartialEntity,
    
    
)

fun PopulatedTopicItemWithGroup.asExternalModel() = TopicItemWithGroup(
    id = partialEntity.id,
    title = partialEntity.title,
    author = author.asExternalModel(),
    created = partialEntity.created,
    lastUpdated = partialEntity.lastUpdated,
    commentCount = partialEntity.commentCount,
    tags = postTags.map(GroupTopicTagEntity::asExternalModel),
    coverUrl = partialEntity.coverUrl,
    url = partialEntity.url,
    uri = partialEntity.uri,
    group = group.asExternalModel()
    
    
)
