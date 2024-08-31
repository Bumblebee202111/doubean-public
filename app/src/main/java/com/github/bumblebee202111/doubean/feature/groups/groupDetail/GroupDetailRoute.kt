package com.github.bumblebee202111.doubean.feature.groups.groupDetail

import kotlinx.serialization.Serializable

@Serializable
data class GroupDetailRoute(
    val groupId: String,
    val defaultTabId: String? = null,
)