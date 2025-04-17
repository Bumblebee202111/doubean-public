package com.github.bumblebee202111.doubean.feature.groups.home

import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.groups.GroupItemWithIntroInfo

sealed interface DayRankingUiState {
    data object Hidden : DayRankingUiState
    data class Success(val items: List<GroupItemWithIntroInfo>) : DayRankingUiState
    data object Loading : DayRankingUiState
    data class Error(val error: AppError) : DayRankingUiState
}