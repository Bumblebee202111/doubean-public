package com.github.bumblebee202111.doubean.feature.subjects.book

import com.github.bumblebee202111.doubean.model.Book
import com.github.bumblebee202111.doubean.model.SubjectWithInterest

sealed interface BookUiState {
    data class Success(val book: SubjectWithInterest<Book>) : BookUiState
    data object Loading : BookUiState
    data object Error : BookUiState
}

