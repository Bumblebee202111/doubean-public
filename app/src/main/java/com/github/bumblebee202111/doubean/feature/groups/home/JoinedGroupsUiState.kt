package com.github.bumblebee202111.doubean.feature.groups.home

import com.github.bumblebee202111.doubean.model.groups.SimpleGroup
import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class JoinedGroupsUiState(
    val isLoading: Boolean = true,
    val groups: List<SimpleGroup>? = null,
    val errorMessage: UiMessage? = null,
)