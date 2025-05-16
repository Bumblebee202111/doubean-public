package com.github.bumblebee202111.doubean.feature.doulists.common

import com.github.bumblebee202111.doubean.model.doulists.DouLists
import com.github.bumblebee202111.doubean.ui.model.UiMessage

sealed interface UserDouListsUiState {
    data object Loading : UserDouListsUiState
    data class Success(val douLists: DouLists) : UserDouListsUiState
    data class Error(val message: UiMessage) : UserDouListsUiState
}
