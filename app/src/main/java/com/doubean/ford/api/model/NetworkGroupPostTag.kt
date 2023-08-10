package com.doubean.ford.api.model

import com.doubean.ford.data.db.model.GroupPostTagEntity

data class NetworkGroupPostTag(
    val id: String,
    val name: String,
    val groupId: String,
)

fun NetworkGroupPostTag.asEntity(groupId: String) = GroupPostTagEntity(
    id = id,
    name = name,
    groupId = groupId
)