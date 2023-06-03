package com.doubean.ford.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.doubean.ford.model.GroupDetail
import com.doubean.ford.model.GroupTab

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
    val followedGroup: FollowedGroupEntity?,
)

data class PopulatedGroupTab(
    @Embedded
    val entity: GroupTabEntity,
    @Relation(parentColumn = "id",
        entityColumn = "tab_id")
    val followedTab: FollowedGroupTabEntity?,
)

fun PopulatedGroupDetail.asExternalModel() = GroupDetail(
    id = partialEntity.id,
    name = partialEntity.name,
    memberCount = partialEntity.memberCount,
    postCount = partialEntity.postCount,
    shareUrl = partialEntity.shareUrl,
    url = partialEntity.url,
    uri = partialEntity.uri,
    avatarUrl = partialEntity.avatarUrl,
    memberName = partialEntity.memberName,
    dateCreated = partialEntity.dateCreated,
    description = partialEntity.description,
    tabs = tabs.map { it.asExternalModel() },
    color = partialEntity.color,
    isFollowed = followedGroup != null
)

fun PopulatedGroupTab.asExternalModel() = GroupTab(
    id = entity.id,
    name = entity.name,
    seq = entity.seq,
    isFollowed = followedTab != null
)