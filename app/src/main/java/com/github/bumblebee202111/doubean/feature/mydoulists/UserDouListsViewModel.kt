package com.github.bumblebee202111.doubean.feature.mydoulists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserDouListRepository
import com.github.bumblebee202111.doubean.feature.doulists.userdoulists.UserDouListsUiState
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.toUiMessage
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDouListsViewModel @Inject constructor(
    private val userDouListRepository: UserDouListRepository,
    private val authRepository: AuthRepository,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserDouListsUiState>(UserDouListsUiState.Loading)
    val uiState: StateFlow<UserDouListsUiState> = _uiState.asStateFlow()
    var currentUserId: String?=null
    init {
        viewModelScope.launch {
            val userId=authRepository.observeLoggedInUserId().first()
            currentUserId=userId
            if(userId==null){
                _uiState.value = UserDouListsUiState.Error(R.string.title_unlogin.toUiMessage())
                return@launch
            }
            fetchCreatedDouLists(userId=userId)
        }
    }

    private fun fetchCreatedDouLists(userId: String) {
        viewModelScope.launch {
            _uiState.value = UserDouListsUiState.Loading
            when (val result =
                userDouListRepository.getUserOwnedDouLists(
                    userId = userId, publicOnly = false
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
        currentUserId?.let {
            fetchCreatedDouLists(it)
        }
    }
}