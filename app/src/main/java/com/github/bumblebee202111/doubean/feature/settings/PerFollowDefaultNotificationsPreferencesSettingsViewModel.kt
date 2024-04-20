package com.github.bumblebee202111.doubean.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.model.PostSortBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class PerFollowDefaultNotificationsPreferencesSettingsViewModel(private val preferenceStorage: PreferenceStorage) :
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
            private val preferenceStorage: PreferenceStorage,
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