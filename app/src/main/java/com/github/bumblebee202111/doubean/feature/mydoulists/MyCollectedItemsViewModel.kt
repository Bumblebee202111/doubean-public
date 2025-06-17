package com.github.bumblebee202111.doubean.feature.mydoulists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserDouListRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.doulists.common.DouListStateHelper
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.subjects.MarkableSubject
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.toUiMessage
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCollectedItemsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userDouListRepository: UserDouListRepository,
    private val userSubjectRepository: UserSubjectRepository,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyCollectedItemsUiState())
    val uiState = _uiState.asStateFlow()

    val isLoggedIn = authRepository.isLoggedIn().stateInUi(false)

    init {
        fetchMyCollectedItems()
    }

    fun fetchMyCollectedItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val loggedInUserId = authRepository.observeLoggedInUserId().first()
            if (loggedInUserId == null) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = R.string.title_unlogin.toUiMessage())
                }
                return@launch
            }

            when (val result = userDouListRepository.getUserDouListPosts(loggedInUserId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, items = result.data)
                    }
                }

                is AppResult.Error -> {
                    val errorMessage = result.error.asUiMessage()
                    snackbarManager.showMessage(errorMessage)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = errorMessage)
                    }
                }
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