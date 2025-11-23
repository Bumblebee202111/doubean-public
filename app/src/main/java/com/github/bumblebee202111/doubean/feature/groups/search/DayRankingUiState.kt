package com.github.bumblebee202111.doubean.feature.groups.search

import com.github.bumblebee202111.doubean.model.groups.GroupItemWithIntroInfo
import com.github.bumblebee202111.doubean.ui.model.UiMessage

sealed interface DayRankingUiState {
    data class Success(val items: List<GroupItemWithIntroInfo>) : DayRankingUiState
    data object Loading : DayRankingUiState
    data class Error(val errorMessage: UiMessage) : DayRankingUiState
}