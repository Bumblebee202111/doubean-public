package com.github.bumblebee202111.doubean.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.coroutines.AppDispatchers
import com.github.bumblebee202111.doubean.coroutines.Dispatcher
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerFollowDefaultNotificationsPreferencesSettingsViewModel @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @Dispatcher(
        AppDispatchers.IO
    ) private val ioDispatcher: CoroutineDispatcher,
) :
    ViewModel() {
    val enablePostNotifications =
        preferenceStorage.perFollowDefaultEnablePostNotifications.flowOn(ioDispatcher).stateInUi()

    fun toggleEnablePostNotifications() {
        viewModelScope.launch {
            preferenceStorage.setPerFollowDefaultEnablePostNotifications(!enablePostNotifications.value!!)
        }
    }

    val allowDuplicateNotifications =
        preferenceStorage.perFollowDefaultAllowDuplicateNotifications.flowOn(ioDispatcher)
            .stateInUi()

    fun toggleAllowDuplicateNotifications() {
        viewModelScope.launch {
            preferenceStorage.setPerFollowDefaultAllowDuplicateNotifications(!allowDuplicateNotifications.value!!)
        }
    }

    val sortRecommendedPostsBy =
        preferenceStorage.perFollowDefaultSortRecommendedPostsBy.flowOn(ioDispatcher).stateInUi()

    fun setSortRecommendedPostsBy(topicSortBy: TopicSortBy) {
        viewModelScope.launch {
            preferenceStorage.setPerFollowDefaultSortRecommendedPostsBy(topicSortBy)
        }
    }

    val feedRequestPostCountLimit =
        preferenceStorage.perFollowDefaultFeedRequestPostCountLimit.flowOn(ioDispatcher).stateInUi()

    fun setFeedRequestPostCountLimit(feedRequestPostCountLimit: Int) {
        viewModelScope.launch {
            preferenceStorage.setPerFollowDefaultFeedRequestPostCountLimit(feedRequestPostCountLimit)
        }
    }

}