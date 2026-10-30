package com.github.bumblebee202111.doubean.feature.subjects.elessar

import com.github.bumblebee202111.doubean.model.subject.ElessarSubject
import com.github.bumblebee202111.doubean.ui.model.UiMessage

sealed interface ElessarSubjectUiState {
    data object Loading : ElessarSubjectUiState
    data class Success(val elessarSubject: ElessarSubject) : ElessarSubjectUiState
    data class Error(val message: UiMessage) : ElessarSubjectUiState
}