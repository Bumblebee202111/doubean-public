package com.github.bumblebee202111.doubean.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.model.PostSortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerFollowDefaultNotificationsPreferencesSettingsViewModel @Inject constructor(private val preferenceStorage: PreferenceStorage) :
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

}