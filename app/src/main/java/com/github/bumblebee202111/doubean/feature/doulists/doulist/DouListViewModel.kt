package com.github.bumblebee202111.doubean.feature.doulists.doulist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.DouListRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.doulists.common.DouListStateHelper
import com.github.bumblebee202111.doubean.feature.doulists.doulist.navigation.DouListRoute
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.common.DouListPostItem
import com.github.bumblebee202111.doubean.model.doulists.DouList
import com.github.bumblebee202111.doubean.model.subjects.MarkableSubject
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.UiMessage
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DouListViewModel @Inject constructor(
    private val douListRepository: DouListRepository,
    private val userSubjectRepository: UserSubjectRepository,
    authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {
    private val douListId = savedStateHandle.toRoute<DouListRoute>().douListId
    private val _uiState = MutableStateFlow(DouListUiState())
    val uiState: StateFlow<DouListUiState> = _uiState.asStateFlow()

    val isLoggedIn = authRepository.isLoggedIn().stateInUi(false)

    init {
        fetchDouListDetails()
    }

    fun fetchDouListDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            var listDetail: DouList? = null
            var listItems: List<DouListPostItem> = emptyList()
            var errorOccurred = false
            var finalErrorMessage: UiMessage? = null

            when (val result = douListRepository.getDouList(douListId)) {
                is AppResult.Success -> {
                    listDetail = result.data
                }

                is AppResult.Error -> {
                    errorOccurred = true
                    finalErrorMessage = result.error.asUiMessage()
                    snackbarManager.showMessage(finalErrorMessage)
                }
            }

            if (!errorOccurred) {
                when (val result = douListRepository.getDouListPosts(douListId)) {
                    is AppResult.Success -> {
                        listItems = result.data
                    }

                    is AppResult.Error -> {
                        errorOccurred = true
                        finalErrorMessage = result.error.asUiMessage()
                        snackbarManager.showMessage(finalErrorMessage)
                    }
                }
            }

            _uiState.update {
                it.copy(
                    douList = listDetail ?: it.douList,
                    items = if (!errorOccurred) listItems else it.items,
                    isLoading = false,
                    errorMessage = if (errorOccurred) finalErrorMessage else null
                )
            }
        }
    }

    fun markSubject(subject: MarkableSubject) {
        viewModelScope.launch {
            when (val result = userSubjectRepository.addSubjectToInterests(
                type = subject.type,
                id = subject.id,
                newStatus = SubjectInterestStatus.MARK_STATUS_MARK
            )) {
                is AppResult.Success -> {
                    val updatedSubjectWithInterest = result.data
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            items = DouListStateHelper.getUpdatedListWithNewInterest(
                                currentItems = currentUiState.items,
                                updatedSubjectWithInterest = updatedSubjectWithInterest
                            )
                        )
                    }
                }

                is AppResult.Error -> {
                    snackbarManager.showMessage(result.error.asUiMessage())
                }
            }
        }
    }
}