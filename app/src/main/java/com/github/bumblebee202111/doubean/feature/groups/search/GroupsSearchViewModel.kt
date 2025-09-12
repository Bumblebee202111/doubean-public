@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.bumblebee202111.doubean.feature.groups.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.SearchHistoryRepository
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.groups.GroupItemWithIntroInfo
import com.github.bumblebee202111.doubean.model.search.SearchType
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsSearchViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val snackbarManager: SnackbarManager,
) :
    ViewModel() {
    
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    val searchHistory = searchHistoryRepository.getHistory(SearchType.GROUPS).stateInUi(emptyList())

    private val _dayRankingUiState = MutableStateFlow<DayRankingUiState>(DayRankingUiState.Loading)
    val dayRankingUiState = _dayRankingUiState.asStateFlow()

    init {
        viewModelScope.launch {
            when (val result = groupRepository.getDayRanking()) {
                is AppResult.Error -> {
                    snackbarManager.showMessage(result.error.asUiMessage())
                    _dayRankingUiState.value = DayRankingUiState.Error(result.error)
                }

                is AppResult.Success -> {
                    _dayRankingUiState.value = DayRankingUiState.Success(result.data)
                }
            }
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val results =
        _query.flatMapLatest { search ->
            if (search.isBlank()) {
                flowOf(PagingData.empty())
            } else {
                groupRepository.search(search).cachedIn(viewModelScope)
            }

        }.cachedIn(viewModelScope)

    fun onQueryChange(currentInput: String) {
        if (currentInput.isBlank()) {
            this._query.value = currentInput
        }
    }

    fun onSearchTriggered(searchInput: String) {
        val trimmedQuery = searchInput.trim()
        if (trimmedQuery.isNotBlank()) {
            viewModelScope.launch {
                searchHistoryRepository.addSearchTerm(SearchType.GROUPS, trimmedQuery)
            }
        }
        _query.value = trimmedQuery
    }

    fun onDeleteHistoryItem(query: String) {
        viewModelScope.launch {
            searchHistoryRepository.deleteSearchTerm(SearchType.GROUPS, query)
        }
    }

    fun onClearHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearHistory(SearchType.GROUPS)
        }
    }
}

sealed interface DayRankingUiState {
    data class Success(val items: List<GroupItemWithIntroInfo>) : DayRankingUiState
    data object Loading : DayRankingUiState
    data class Error(val error: AppError) : DayRankingUiState
}