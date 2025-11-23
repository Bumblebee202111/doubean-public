package com.github.bumblebee202111.doubean.feature.subjects.ranklist

import com.github.bumblebee202111.doubean.model.subjects.SubjectCollection
import com.github.bumblebee202111.doubean.ui.model.UiMessage

sealed interface RankListUiState {
    data class Success(
        val rankList: SubjectCollection,
        val isLoggedIn: Boolean,
    ) : RankListUiState

    data object Loading : RankListUiState
    data class Error(val message: UiMessage) : RankListUiState
}