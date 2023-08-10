package com.doubean.ford

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.doubean.ford.data.prefs.DataStorePreferenceStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn


class MainActivityViewModel(preferenceStorage: DataStorePreferenceStorage) :
    ViewModel() {
    val enableNotifications = preferenceStorage.preferToReceiveNotifications.flowOn(
        Dispatchers.IO
    ).asLiveData()

    companion object {
        class Factory(
            private val preferenceStorage: DataStorePreferenceStorage,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainActivityViewModel(preferenceStorage) as T
            }
        }
    }
}