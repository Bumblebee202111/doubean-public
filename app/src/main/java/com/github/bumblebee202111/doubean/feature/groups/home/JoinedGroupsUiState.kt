package com.github.bumblebee202111.doubean.feature.groups.home

import com.github.bumblebee202111.doubean.model.groups.SimpleGroup

data class JoinedGroupsUiState(
    val isLoading: Boolean = false,
    val groups: List<SimpleGroup>? = null,
)