package com.github.bumblebee202111.doubean

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.coroutines.AppDispatchers
import com.github.bumblebee202111.doubean.coroutines.Dispatcher
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserRepository
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    preferenceStorage: PreferenceStorage,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val snackbarManager: SnackbarManager,
) :
    ViewModel() {
    val enableNotifications =
        preferenceStorage.preferToReceiveNotifications.flowOn(ioDispatcher).stateInUi()

    val startAppWithGroups =
        preferenceStorage.startAppWithGroups.flowOn(ioDispatcher).take(1).stateInUi()

    val autoImportSessionAtStartup =
        preferenceStorage.preferToAutoImportSessionAtStartup.stateInUi()

    private fun checkAndRefreshToken() {
        viewModelScope.launch {
            val result = authRepository.checkAndRefreshToken()
            if (result is AppResult.Error && authRepository.isLoggedIn().first()) {
                snackbarManager.showMessage(result.error.asUiMessage())
            }
        }
    }

    private fun refreshUser() {
        viewModelScope.launch {
            val currentUserId = authRepository.loggedInUserId.first()
            if (currentUserId != null) {
                val userResult = userRepository.fetchUser(currentUserId)
                if (userResult is AppResult.Error) {
                    snackbarManager.showMessage(userResult.error.asUiMessage())
                }
            }
        }
    }

    init {
        checkAndRefreshToken()
        refreshUser()
    }

}