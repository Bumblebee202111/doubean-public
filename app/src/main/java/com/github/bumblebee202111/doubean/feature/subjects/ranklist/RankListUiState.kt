package com.github.bumblebee202111.doubean.feature.subjects.ranklist

import com.github.bumblebee202111.doubean.model.SubjectCollection
import com.github.bumblebee202111.doubean.model.SubjectWithRankAndInterest

sealed interface RankListUiState {
    data class Success(
        val rankList: SubjectCollection,
        val items: List<SubjectWithRankAndInterest<*>>,
        val isLoggedIn: Boolean,
    ) : RankListUiState

    data object Loading : RankListUiState
    data object Error : RankListUiState
}