@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.bumblebee202111.doubean.feature.subjects.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.SearchSubjectsRepository
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.SubjectsSearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchSubjectsViewModel @Inject constructor(
    private val repository: SearchSubjectsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchResultUiState())
    val uiState = _uiState.asStateFlow()
    private val _uiError = MutableStateFlow<AppError?>(null)
    val uiError = _uiError.asStateFlow()
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage = _uiMessage.asStateFlow()

    private var currentJob: Job? = null

    fun onQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
    }

    fun onSearchTriggered() {
        val query = _uiState.value.query

        if (query.isBlank()) {
            _uiState.value = SearchResultUiState()
            return
        }
        _uiState.value = _uiState.value.copy(
            types = null,
            isLoading = true,
            selectedType = null
        )
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            val result = repository.searchSubjects(
                query = query
            )
            _uiState.value = when (result) {
                is AppResult.Error -> {
                    _uiError.value = result.error
                    _uiState.value.copy(isLoading = false)
                }

                is AppResult.Success -> {
                    val (banned, items, types) = result.data
                    _uiMessage.value = banned
                    _uiState.value.copy(
                        items = items,
                        types = types,
                        selectedType = types?.firstOrNull()?.type,
                        isLoading = false
                    )
                }
            }

        }
    }

    fun onTypeSelected(type: SubjectsSearchType) {
        if (type == _uiState.value.selectedType) return
        _uiState.value = _uiState.value.copy(selectedType = type)
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                selectedType = type
            )

            val result = repository.searchSubjects(
                query = _uiState.value.query, type = type
            )
            _uiState.value = when (result) {
                is AppResult.Error -> {
                    _uiError.value = result.error
                    _uiState.value.copy(isLoading = false)
                }

                is AppResult.Success -> {
                    val (banned, items) = result.data
                    _uiMessage.value = banned
                    _uiState.value.copy(
                        items = items,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onUiErrorShown() {
        _uiError.value = null
    }

    fun onUiMessageShown() {
        _uiMessage.value = null
    }
}
