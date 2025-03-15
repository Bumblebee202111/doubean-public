package com.github.bumblebee202111.doubean.feature.subjects.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.SearchSubjectsRepository
import com.github.bumblebee202111.doubean.feature.subjects.search.navigation.SearchSubjectsRoute
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class SearchSubjectsViewModel @Inject constructor(
    private val searchSubjectsRepository: SearchSubjectsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val type = savedStateHandle.toRoute<SearchSubjectsRoute>().type
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    val searchResultUiState = _query.flatMapLatest { query ->
        if (query.isEmpty()) {
            flowOf<SearchResultUiState>(SearchResultUiState.EmptyQuery)
        } else {
            flow {
                emit(searchSubjectsRepository.searchSubjects(query = query, type = type).fold(
                    onSuccess = { subjects ->
                        SearchResultUiState.Success(subjects)
                    },
                    onFailure = {
                        SearchResultUiState.LoadFailed
                    }
                ))
            }
        }
    }.stateInUi(SearchResultUiState.Loading)

    fun onSearchTriggered(newQuery: String) {
        _query.value = newQuery.trim()
    }
}
