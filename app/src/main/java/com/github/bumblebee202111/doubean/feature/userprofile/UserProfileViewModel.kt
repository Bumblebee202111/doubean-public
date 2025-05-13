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
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val userIdArg = savedStateHandle.toRoute<UserProfileRoute>().userId

    private val _uiState = MutableStateFlow(UserProfileUiState(isLoading = true))
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val currentUserId = authRepository.observeLoggedInUserId().first()
            val loggedIn = currentUserId != null
            val targetingCurrentUser = (userIdArg == null)
            val idToFetch: String? = if (targetingCurrentUser) currentUserId else userIdArg
            _uiState.update {
                it.copy(
                    isTargetingCurrentUser = targetingCurrentUser,
                    isLoggedIn = loggedIn
                )
            }
            if (idToFetch == null) {
                _uiState.update {
                    it.copy(isLoading = false, user = null)
                }
                return@launch
            }

            when (val userResult = userRepository.getUserDetail(idToFetch)) {
                is AppResult.Success -> {
                    val user = userResult.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            errorMessage = null
                        )
                    }
                }

                is AppResult.Error -> {
                    val errorMessage = userResult.error.asUiMessage()
                    snackbarManager.showSnackBar(errorMessage)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = null,
                            errorMessage = errorMessage
                        )
                    }
                }
            }
        }
    }

}
