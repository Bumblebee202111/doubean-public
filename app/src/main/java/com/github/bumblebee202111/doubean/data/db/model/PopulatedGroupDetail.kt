package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.github.bumblebee202111.doubean.model.groups.GroupDetail
import com.github.bumblebee202111.doubean.model.groups.GroupTab

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
    @Relation(
        parentColumn = "id",
        entityColumn = "group_id",
    )
    val notificationTarget: GroupGroupNotificationTargetEntity?,
)

data class PopulatedGroupTab(
    @Embedded
    val entity: GroupTabEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tab_id"
    )
    val favoriteTab: FavoriteGroupTabEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "tab_id",
    )
    val notificationTarget: GroupTabNotificationTargetEntity?,
)

fun PopulatedGroupDetail.asExternalModel() = GroupDetail(
    id = partialEntity.id,
    name = partialEntity.name,
    memberCount = partialEntity.memberCount,
    topicCount = partialEntity.topicCount,
    shareUrl = partialEntity.shareUrl,
    url = partialEntity.url,
    uri = partialEntity.uri,
    avatarUrl = partialEntity.avatarUrl,
    memberName = partialEntity.memberName,
    dateCreated = partialEntity.dateCreated,
    description = partialEntity.description,
    tabs = tabs.map { it.asExternalModel() },
    color = partialEntity.color,
    memberRole = partialEntity.memberRole,
    isSubscriptionEnabled = partialEntity.isSubscriptionEnabled,
    isSubscribed = partialEntity.isSubscribed,
    isFavorited = favoriteGroup != null,
    notificationPreferences = notificationTarget?.toGroupNotificationPreferences()
)

fun PopulatedGroupTab.asExternalModel() = GroupTab(
    id = entity.id,
    name = entity.name,
    seq = entity.seq,
    isFavorite = favoriteTab != null,
    notificationPreferences = notificationTarget?.toGroupNotificationPreferences()
)