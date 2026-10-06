package com.github.bumblebee202111.doubean.feature.subjects.search

import com.github.bumblebee202111.doubean.model.subject.SearchResultSubjectItem
import com.github.bumblebee202111.doubean.model.subject.SubjectSubTag
import com.github.bumblebee202111.doubean.model.subject.SubjectsSearchType
import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class SearchResultUiState(
    val query: String = "",
    val items: List<SearchResultSubjectItem>? = null,
    val types: List<SubjectSubTag>? = null,
    val selectedType: SubjectsSearchType? = null,
    val isLoading: Boolean = false,
    val errorMessage: UiMessage? = null,
)
