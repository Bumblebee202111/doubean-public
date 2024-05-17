package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.github.bumblebee202111.doubean.model.RecommendedGroupItem
import com.github.bumblebee202111.doubean.model.RecommendedGroupItemGroup


class PopulatedRecommendedGroup(
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
        entity = PostEntity::class
    )
    val posts: List<PopulatedPostItem>,
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
    
)

fun PopulatedRecommendedGroupItemGroup.asExternalModel() = RecommendedGroupItemGroup(
    id = partialEntity.id,
    name = partialEntity.name,
    memberCount = partialEntity.memberCount,
    memberName = partialEntity.memberName,
    postCount = partialEntity.postCount,
    shareUrl = partialEntity.shareUrl,
    url = partialEntity.url,
    uri = partialEntity.uri,
    avatarUrl = partialEntity.avatarUrl,
    color = partialEntity.color,
    shortDescription = partialEntity.shortDescription,
    isFavorite = favoriteGroup != null
)
