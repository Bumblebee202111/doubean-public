package com.github.bumblebee202111.doubean

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.coroutines.AppDispatchers
import com.github.bumblebee202111.doubean.coroutines.Dispatcher
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserRepository
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    preferenceStorage: PreferenceStorage,
    private val authRepository: AuthRepository,
    userRepository: UserRepository,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) :
    ViewModel() {
    val enableNotifications =
        preferenceStorage.preferToReceiveNotifications.flowOn(ioDispatcher).stateInUi()

    val startAppWithGroups =
        preferenceStorage.startAppWithGroups.flowOn(ioDispatcher).take(1).stateInUi()

    val autoImportSessionAtStartup =
        preferenceStorage.preferToAutoImportSessionAtStartup.stateInUi()

    private val _uiError = MutableStateFlow<AppError?>(null)
    val uiError = _uiError.asStateFlow()

    private fun checkAndRefreshToken() {
        viewModelScope.launch {
            val result = authRepository.checkAndRefreshToken()
            if (result is AppResult.Error && authRepository.isLoggedIn().first()) {
                _uiError.value = result.error
            }
        }
    }

    fun clearUiError() {
        _uiError.value = null
    }

    init {
        viewModelScope.launch {
            authRepository.observeLoggedInUserId().collect {
                if (it != null) {
                    viewModelScope.launch {
                        userRepository.fetchUser(it)
                    }
                }
            }
        }

        checkAndRefreshToken()
    }

}