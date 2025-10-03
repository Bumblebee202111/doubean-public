package com.github.bumblebee202111.doubean.feature.doulists.createddoulists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserDouListRepository
import com.github.bumblebee202111.doubean.feature.doulists.createddoulists.navigation.CreatedDouListsRoute
import com.github.bumblebee202111.doubean.feature.doulists.userdoulists.UserDouListsUiState
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatedDouListsViewModel @Inject constructor(
    private val userDouListRepository: UserDouListRepository,
    private val authRepository: AuthRepository,
    private val snackbarManager: SnackbarManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val routeArgs: CreatedDouListsRoute = savedStateHandle.toRoute()
    val userId = routeArgs.userId

    private val _uiState = MutableStateFlow<UserDouListsUiState>(UserDouListsUiState.Loading)
    val uiState: StateFlow<UserDouListsUiState> = _uiState.asStateFlow()

    init {
        fetchCreatedDouLists()
    }

    private fun fetchCreatedDouLists() {
        viewModelScope.launch {
            _uiState.value = UserDouListsUiState.Loading
            val loggedInUserId: String? = authRepository.loggedInUserId.first()
            val isViewingOwnLists = (loggedInUserId != null && loggedInUserId == userId)
            when (val result =
                userDouListRepository.getUserOwnedDouLists(
                    userId = userId,
                    publicOnly = !isViewingOwnLists
                )) {
                is AppResult.Success -> {
                    _uiState.value = UserDouListsUiState.Success(result.data)
                }

                is AppResult.Error -> {
                    val uiMessage = result.error.asUiMessage()

                    snackbarManager.showMessage(uiMessage)
                    _uiState.value = UserDouListsUiState.Error(uiMessage)
                }
            }
        }
    }

    fun onRetry() {
        fetchCreatedDouLists()
    }
}