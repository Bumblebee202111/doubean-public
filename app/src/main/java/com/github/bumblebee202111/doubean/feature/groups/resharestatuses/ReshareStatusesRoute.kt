package com.github.bumblebee202111.doubean.feature.groups.resharestatuses

import kotlinx.serialization.Serializable

@Serializable
data class ReshareStatusesRoute(
    val topicId: String,
)