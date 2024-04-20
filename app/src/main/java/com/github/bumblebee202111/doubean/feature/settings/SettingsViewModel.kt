package com.github.bumblebee202111.doubean.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class SettingsViewModel(private val preferenceStorage: PreferenceStorage) : ViewModel() {
    val enableNotifications =
        preferenceStorage.preferToReceiveNotifications.flowOn(Dispatchers.IO).asLiveData()

    fun toggleEnableNotifications() {
        viewModelScope.launch {
            preferenceStorage.preferToReceiveNotifications(!enableNotifications.value!!)
        }
    }

    companion object {
        class Factory(
            private val preferenceStorage: PreferenceStorage,
        ) : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(
                    preferenceStorage
                ) as T
            }

        }
    }
}