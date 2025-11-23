package com.github.bumblebee202111.doubean.feature.userprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserRepository
import com.github.bumblebee202111.doubean.feature.userprofile.navigation.UserProfileRoute
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.toUiMessage
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val snackbarManager: SnackbarManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val navigatedUserId = savedStateHandle.toRoute<UserProfileRoute>().userId

    private val _uiState = MutableStateFlow(UserProfileUiState(isLoading = true))
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private val retryTrigger = MutableStateFlow(0)

    private val baseTargetUserIdFlow: Flow<String?> =
        authRepository.loggedInUserId.map { loggedInUserId ->
            val isTargetingCurrentUserProfile = (navigatedUserId == null)
            _uiState.update {
                it.copy(
                    isTargetingCurrentUser = isTargetingCurrentUserProfile,
                    isLoggedIn = (loggedInUserId != null)
                )
            }
            if (isTargetingCurrentUserProfile) loggedInUserId else navigatedUserId
        }.distinctUntilChanged()

    private val targetUserIdFlow: Flow<String?> =
        baseTargetUserIdFlow.combine(retryTrigger) { userId, _ -> userId }

    init {
        viewModelScope.launch {
            targetUserIdFlow.collect { newTargetId ->
                if (newTargetId != null) {
                    executeLoadUserProfile(newTargetId)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = null,
                            communityContribution = null,
                            errorMessage = null
                        )
                    }
                }
            }
        }
    }

    private fun executeLoadUserProfile(userIdToFetch: String) {
        viewModelScope.launch {
            if (_uiState.value.user?.id != userIdToFetch) {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        user = null,
                        communityContribution = null,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            when (val userResult = userRepository.getUserDetail(userIdToFetch)) {
                is AppResult.Success -> {
                    val fetchedUser = userResult.data
                    _uiState.update {
                        it.copy(
                            user = fetchedUser,
                            errorMessage = null
                        )
                    }
                    if (fetchedUser.hasCommunityContribution) {
                        fetchUserContributions(userIdToFetch)
                    } else {
                        _uiState.update { it.copy(isLoading = false, communityContribution = null) }
                    }
                }

                is AppResult.Error -> {
                    val errorMessage = userResult.error.asUiMessage()
                    snackbarManager.showMessage(errorMessage)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = if (_uiState.value.user?.id == userIdToFetch) _uiState.value.user else null,
                            communityContribution = null,
                            errorMessage = errorMessage
                        )
                    }
                }
            }
        }
    }

    private fun fetchUserContributions(userId: String) {
        viewModelScope.launch {
            when (val contributionsResult = userRepository.getUserCommunityContributions(userId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            communityContribution = contributionsResult.data
                        )
                    }
                }

                is AppResult.Error -> {
                    snackbarManager.showMessage(contributionsResult.error.asUiMessage())
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            communityContribution = null
                        )
                    }
                }
            }
        }
    }

    fun retry() {
        retryTrigger.value++
    }

    fun showInfoMessage(message: String) {
        snackbarManager.showMessage(message.toUiMessage())
    }
}
