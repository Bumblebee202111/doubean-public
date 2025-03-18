package com.github.bumblebee202111.doubean.feature.subjects

import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.SubjectModule

sealed interface SubjectModulesUiState {
    data class Success(
        val modules: List<SubjectModule>,
        val isLoggedIn: Boolean,
    ) : SubjectModulesUiState

    data class Error(val error: AppError? = null) : SubjectModulesUiState
    data object Loading : SubjectModulesUiState
}
