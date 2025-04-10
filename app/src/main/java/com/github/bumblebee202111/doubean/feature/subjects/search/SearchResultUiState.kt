package com.github.bumblebee202111.doubean.feature.subjects.search

import com.github.bumblebee202111.doubean.model.SearchResultSubjectItem
import com.github.bumblebee202111.doubean.model.SubjectSubTag
import com.github.bumblebee202111.doubean.model.SubjectsSearchType

data class SearchResultUiState(
    val query: String = "",
    val items: List<SearchResultSubjectItem>? = null,
    val types: List<SubjectSubTag>? = null,
    val selectedType: SubjectsSearchType? = null,
    val isLoading: Boolean = false,
)
