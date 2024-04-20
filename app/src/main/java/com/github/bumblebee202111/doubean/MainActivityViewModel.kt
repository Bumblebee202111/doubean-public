package com.github.bumblebee202111.doubean

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn


class MainActivityViewModel(preferenceStorage: PreferenceStorage) :
    ViewModel() {
    val enableNotifications = preferenceStorage.preferToReceiveNotifications.flowOn(
        Dispatchers.IO
    ).asLiveData()

    companion object {
        class Factory(
            private val preferenceStorage: PreferenceStorage,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainActivityViewModel(preferenceStorage) as T
            }
        }
    }
}