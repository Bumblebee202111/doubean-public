package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.github.bumblebee202111.doubean.model.GroupDetail
import com.github.bumblebee202111.doubean.model.GroupTab

data class PopulatedGroupDetail(
    @Embedded
    val partialEntity: GroupDetailPartialEntity,
    @Relation(
        entity = GroupTabEntity::class,
        parentColumn = "id",
        entityColumn = "group_id",
    )
    val tabs: List<PopulatedGroupTab>,
    @Relation(
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val favoriteGroup: FavoriteGroupEntity?,
)

data class PopulatedGroupTab(
    @Embedded
    val entity: GroupTabEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tab_id"
    )
    val favoriteTab: FavoriteGroupTabEntity?,
)

fun PopulatedGroupDetail.asExternalModel() = GroupDetail(
    id = partialEntity.id,
    name = partialEntity.name,
    memberCount = partialEntity.memberCount,
    postCount = partialEntity.topicCount,
    shareUrl = partialEntity.shareUrl,
    url = partialEntity.url,
    uri = partialEntity.uri,
    avatarUrl = partialEntity.avatarUrl,
    memberName = partialEntity.memberName,
    dateCreated = partialEntity.dateCreated,
    description = partialEntity.description,
    tabs = tabs.map { it.asExternalModel() },
    color = partialEntity.color,
    isFavorited = favoriteGroup != null,
    enableNotifications = favoriteGroup?.enablePostNotifications,
    allowDuplicateNotifications = favoriteGroup?.allowDuplicateNotifications,
    sortRecommendedTopicsBy = favoriteGroup?.sortRecommendedPostsBy,
    feedRequestTopicCountLimit = favoriteGroup?.feedRequestPostCountLimit,
)

fun PopulatedGroupTab.asExternalModel() = GroupTab(
    id = entity.id,
    name = entity.name,
    seq = entity.seq,
    isFavorite = favoriteTab != null,
    enableNotifications = favoriteTab?.enablePostNotifications,
    allowDuplicateNotifications = favoriteTab?.allowDuplicateNotifications,
    sortRecommendedTopicsBy = favoriteTab?.sortRecommendedPostsBy,
    feedRequestTopicCountLimit = favoriteTab?.feedRequestPostCountLimit,
)