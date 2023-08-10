package com.doubean.ford.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.doubean.ford.data.prefs.DataStorePreferenceStorage
import com.doubean.ford.model.PostSortBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class PerFollowDefaultNotificationsPreferencesSettingsViewModel(private val preferenceStorage: DataStorePreferenceStorage) :
    ViewModel() {
    val enablePostNotifications =
        preferenceStorage.perFollowDefaultEnablePostNotifications.flowOn(Dispatchers.IO)
            .asLiveData()

    fun toggleEnablePostNotifications() {
        viewModelScope.launch {
            preferenceStorage.setPerFollowDefaultEnablePostNotifications(!enablePostNotifications.value!!)
        }
    }

    val allowDuplicateNotifications =
        preferenceStorage.perFollowDefaultAllowDuplicateNotifications.flowOn(Dispatchers.IO)
            .asLiveData()

    fun toggleAllowDuplicateNotifications() {
        viewModelScope.launch {
            preferenceStorage.setPerFollowDefaultAllowDuplicateNotifications(!allowDuplicateNotifications.value!!)
        }
    }

    val sortRecommendedPostsBy =
        preferenceStorage.perFollowDefaultSortRecommendedPostsBy.flowOn(Dispatchers.IO).asLiveData()

    fun setSortRecommendedPostsBy(postSortBy: PostSortBy) {
        viewModelScope.launch {
            preferenceStorage.setPerFollowDefaultSortRecommendedPostsBy(postSortBy)
        }
    }

    val feedRequestPostCountLimit =
        preferenceStorage.perFollowDefaultFeedRequestPostCountLimit.flowOn(Dispatchers.IO)
            .asLiveData()

    fun setFeedRequestPostCountLimit(feedRequestPostCountLimit: Int) {
        viewModelScope.launch {
            preferenceStorage.setPerFollowDefaultFeedRequestPostCountLimit(feedRequestPostCountLimit)
        }
    }

    companion object {
        class Factory(
            private val preferenceStorage: DataStorePreferenceStorage,
        ) : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PerFollowDefaultNotificationsPreferencesSettingsViewModel(
                    preferenceStorage
                ) as T
            }

        }
    }
}