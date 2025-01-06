package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.github.bumblebee202111.doubean.model.RecommendedGroupItem
import com.github.bumblebee202111.doubean.model.RecommendedGroupItemGroup


data class PopulatedRecommendedGroup(
    @Embedded
    val entity: RecommendedGroupEntity,

    @Relation(
        parentColumn = "group_id",
        entityColumn = "id",
        entity = GroupEntity::class
    )
    val group: PopulatedRecommendedGroupItemGroup,

    @Relation(
        parentColumn = "group_id",
        entityColumn = "id",
        entity = TopicEntity::class
    )
    val topics: List<PopulatedTopicItem>,
)

data class PopulatedRecommendedGroupItemGroup(
    @Embedded
    val partialEntity: RecommendedGroupItemGroupPartialEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val favoriteGroup: FavoriteGroupEntity?,
)


fun PopulatedRecommendedGroup.asExternalModel() = RecommendedGroupItem(
    no = entity.no,
    group = group.asExternalModel(),
    //topics = topics.map { it.asExternalModel() },
)

fun PopulatedRecommendedGroupItemGroup.asExternalModel() = RecommendedGroupItemGroup(
    id = partialEntity.id,
    name = partialEntity.name,
    memberCount = partialEntity.memberCount,
    memberName = partialEntity.memberName,
    topicCount = partialEntity.topicCount,
    shareUrl = partialEntity.shareUrl,
    url = partialEntity.url,
    uri = partialEntity.uri,
    avatarUrl = partialEntity.avatarUrl,
    color = partialEntity.color,
    descAbstract = partialEntity.descAbstract,
    isFavorite = favoriteGroup != null
)
