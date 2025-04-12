package com.github.bumblebee202111.doubean.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.coroutines.AppDispatchers
import com.github.bumblebee202111.doubean.coroutines.Dispatcher
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDefaultNotificationPreferencesSettingsViewModel @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @Dispatcher(
        AppDispatchers.IO
    ) private val ioDispatcher: CoroutineDispatcher,
) :
    ViewModel() {
    val defaultGroupNotificationPreferences =
        preferenceStorage.defaultGroupNotificationPreferences.flowOn(ioDispatcher).stateInUi()

    fun toggleEnableNotifications() {
        val preferences = defaultGroupNotificationPreferences.value ?: return
        viewModelScope.launch {
            preferenceStorage.setDefaultGroupNotificationPreferences(preferences.run {
                copy(notificationsEnabled = !notificationsEnabled)
            })
        }
    }

    fun toggleNotifyOnUpdates() {
        val preferences = defaultGroupNotificationPreferences.value ?: return
        viewModelScope.launch {
            preferenceStorage.setDefaultGroupNotificationPreferences(preferences.run {
                copy(notifyOnUpdates = !notifyOnUpdates)
            })
        }
    }

    fun setSortBy(sortBy: TopicSortBy) {
        val preferences = defaultGroupNotificationPreferences.value ?: return
        viewModelScope.launch {
            preferenceStorage.setDefaultGroupNotificationPreferences(preferences.run {
                copy(sortBy = sortBy)
            })
        }
    }

    fun setMaxTopicNotificationsPerFetch(maxTopicNotificationsPerFetch: Int) {
        val preferences = defaultGroupNotificationPreferences.value ?: return
        viewModelScope.launch {
            preferenceStorage.setDefaultGroupNotificationPreferences(preferences.run {
                copy(maxTopicNotificationsPerFetch = maxTopicNotificationsPerFetch)
            })
        }
    }

}