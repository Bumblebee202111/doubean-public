package com.github.bumblebee202111.doubean.feature.groups.home

import com.github.bumblebee202111.doubean.model.groups.TopicItemWithGroup
import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class RecentTopicsFeedUiState(
    val isLoading: Boolean = false,
    val topics: List<TopicItemWithGroup>? = null,
    val errorMessage: UiMessage? = null,
)