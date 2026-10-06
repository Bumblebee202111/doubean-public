package com.github.bumblebee202111.doubean.feature.groups.home

import com.github.bumblebee202111.doubean.model.group.GroupItemWithIntroInfo
import com.github.bumblebee202111.doubean.ui.model.UiMessage

sealed interface DayRankingUiState {
    data object Hidden : DayRankingUiState
    data class Success(val items: List<GroupItemWithIntroInfo>) : DayRankingUiState
    data object Loading : DayRankingUiState
    data class Error(val errorMessage: UiMessage) : DayRankingUiState
}