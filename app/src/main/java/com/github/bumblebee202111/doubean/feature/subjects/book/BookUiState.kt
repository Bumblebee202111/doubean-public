package com.github.bumblebee202111.doubean.feature.subjects.book

import com.github.bumblebee202111.doubean.model.BookDetail
import com.github.bumblebee202111.doubean.model.SubjectInterestWithUserList

sealed interface BookUiState {
    data class Success(
        val book: BookDetail,
        val interests: SubjectInterestWithUserList,
        val isLoggedIn: Boolean,
    ) : BookUiState
    data object Loading : BookUiState
    data object Error : BookUiState
}

