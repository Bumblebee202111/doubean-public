package com.github.bumblebee202111.doubean

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.coroutines.AppDispatchers
import com.github.bumblebee202111.doubean.coroutines.Dispatcher
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserRepository
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    preferenceStorage: PreferenceStorage,
    authRepository: AuthRepository,
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
    }

}