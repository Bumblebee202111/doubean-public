package com.github.bumblebee202111.doubean.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val authRepository: AuthRepository,
) :
    ViewModel() {
    val enableNotifications =
        preferenceStorage.preferToReceiveNotifications.stateInUi()
    val startupTab = preferenceStorage.startupTab.stateInUi()
    val visibleTabs = preferenceStorage.visibleTabs.stateInUi()
    val autoImportSessionAtStartup =
        preferenceStorage.preferToAutoImportSessionAtStartup.stateInUi()
    val isLoggedIn = authRepository.isLoggedIn().stateInUi()

    fun toggleEnableNotifications() {
        viewModelScope.launch {
            preferenceStorage.preferToReceiveNotifications(!enableNotifications.value!!)
        }
    }

    fun setStartupTab(tabName: String) {
        viewModelScope.launch { preferenceStorage.setStartupTab(tabName) }
    }

    fun setVisibleTabs(tabs: Set<String>) {
        viewModelScope.launch { preferenceStorage.setVisibleTabs(tabs) }
    }

    fun toggleAutoImportSessionAtStartup() {
        val oldValue = autoImportSessionAtStartup.value ?: return
        viewModelScope.launch {
            preferenceStorage.preferToAutoImportSessionAtStartup(!oldValue)
        }

    }

    fun logout() {
        viewModelScope.launch {
            authRepository.unregisterDevice()
        }
    }
}