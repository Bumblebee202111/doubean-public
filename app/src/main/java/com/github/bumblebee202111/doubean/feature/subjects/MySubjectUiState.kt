package com.github.bumblebee202111.doubean.feature.subjects

import com.github.bumblebee202111.doubean.model.MySubject

sealed interface MySubjectUiState {
    data class Success(val userId: String, val mySubject: MySubject) : MySubjectUiState

    data object NotLoggedIn : MySubjectUiState
    data object Error : MySubjectUiState
    data object Loading : MySubjectUiState
}