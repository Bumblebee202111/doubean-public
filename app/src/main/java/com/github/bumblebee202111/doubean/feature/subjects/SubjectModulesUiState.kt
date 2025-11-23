package com.github.bumblebee202111.doubean.feature.subjects

import com.github.bumblebee202111.doubean.model.subjects.SubjectModule
import com.github.bumblebee202111.doubean.ui.model.UiMessage

sealed interface SubjectModulesUiState {
    data class Success(
        val modules: List<SubjectModule>,
        val isLoggedIn: Boolean,
    ) : SubjectModulesUiState

    data class Error(val message: UiMessage) : SubjectModulesUiState
    data object Loading : SubjectModulesUiState
}
