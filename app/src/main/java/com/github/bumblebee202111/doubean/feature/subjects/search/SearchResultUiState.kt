package com.github.bumblebee202111.doubean.feature.subjects.search

import com.github.bumblebee202111.doubean.model.SearchResultSubjectItem

sealed interface SearchResultUiState {
    data object Loading : SearchResultUiState
    data object EmptyQuery : SearchResultUiState
    data object LoadFailed : SearchResultUiState
    data class Success(
        val subjects: List<SearchResultSubjectItem>,
    ) : SearchResultUiState
}
