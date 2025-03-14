package com.github.bumblebee202111.doubean.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val authRepository: AuthRepository,
) :
    ViewModel() {
    val enableNotifications =
        preferenceStorage.preferToReceiveNotifications.flowOn(Dispatchers.IO).stateInUi()
    val startAppWithGroups =
        preferenceStorage.startAppWithGroups.flowOn(Dispatchers.IO).stateInUi()
    val autoImportSessionAtStartup =
        preferenceStorage.preferToAutoImportSessionAtStartup.stateInUi()
    val isLoggedIn = authRepository.isLoggedIn().stateInUi()

    fun toggleEnableNotifications() {
        viewModelScope.launch {
            preferenceStorage.preferToReceiveNotifications(!enableNotifications.value!!)
        }
    }

    fun toggleSetGroupsAsStartDestination() {
        viewModelScope.launch {
            preferenceStorage.setStartAppWithGroups(!startAppWithGroups.value!!)
        }
    }

    fun toggleAutoImportSessionAtStartup() {
        val oldValue = autoImportSessionAtStartup.value ?: return
        viewModelScope.launch {
            preferenceStorage.preferToAutoImportSessionAtStartup(!oldValue)
        }

    }

    fun triggerAutoImport() {
        viewModelScope.launch {
            authRepository.syncSessionFromDoubanPrefs()
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.clearSavedSession()
        }
    }
}