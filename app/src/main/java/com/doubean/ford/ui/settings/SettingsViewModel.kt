package com.doubean.ford.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.doubean.ford.data.prefs.DataStorePreferenceStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class SettingsViewModel(private val preferenceStorage: DataStorePreferenceStorage) : ViewModel() {
    val enableNotifications =
        preferenceStorage.preferToReceiveNotifications.flowOn(Dispatchers.IO).asLiveData()

    fun toggleEnableNotifications() {
        viewModelScope.launch {
            preferenceStorage.preferToReceiveNotifications(!enableNotifications.value!!)
        }
    }

    companion object {
        class Factory(
            private val preferenceStorage: DataStorePreferenceStorage,
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