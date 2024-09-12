package com.github.bumblebee202111.doubean.feature.groups.groupTab

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.UserGroupRepository
import com.github.bumblebee202111.doubean.model.TopicSortBy
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = GroupTabViewModel.Factory::class)
class GroupTabViewModel @AssistedInject constructor(
    @Assisted("groupId") val groupId: String,
    @Assisted("tabId") val tabId: String?,
    private val groupRepository: GroupRepository,
    private val userGroupRepository: UserGroupRepository,
    private val preferenceStorage: PreferenceStorage,
) : ViewModel() {

    private val _sortBy = MutableStateFlow<TopicSortBy?>(null)

    val sortBy = _sortBy.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val topicsPagingData =
        _sortBy.flatMapLatest { sortBy ->
            sortBy?.let {
                groupRepository.getTopicsPagingData(
                    groupId, tabId,
                    it
                ).cachedIn(viewModelScope)
            } ?: emptyFlow()
        }.cachedIn(viewModelScope)

    var shouldDisplayFavoritedTab by mutableStateOf(false)

    var shouldDisplayUnfavoritedTab by mutableStateOf(false)

    fun updateSortBy(topicSortBy: TopicSortBy) {
        _sortBy.value = topicSortBy
    }

    fun addFavorite() {
        viewModelScope.launch {
            userGroupRepository.addFavoriteTab(
                groupId = groupId,
                tabId = tabId!!,
                enablePostNotifications = preferenceStorage.perFollowDefaultEnablePostNotifications.first(),
                allowsDuplicateNotifications = preferenceStorage.perFollowDefaultAllowDuplicateNotifications.first(),
                sortRecommendedPostsBy = preferenceStorage.perFollowDefaultSortRecommendedPostsBy.first(),
                feedRequestPostCountLimit = preferenceStorage.perFollowDefaultFeedRequestPostCountLimit.first()
            )
            shouldDisplayFavoritedTab = true
        }
    }

    fun removeFavorite() {
        viewModelScope.launch {
            userGroupRepository.removeFavoriteTab(tabId!!)
            shouldDisplayUnfavoritedTab = false
        }
    }

    fun clearFavoritedTabState() {
        shouldDisplayFavoritedTab = false
    }

    fun clearUnfavoritedTabState() {
        shouldDisplayUnfavoritedTab = false
    }

    fun saveNotificationsPreference(
        enableNotifications: Boolean,
        allowNotificationUpdates: Boolean,
        sortRecommendedTopicsBy: TopicSortBy,
        numberOfTopicsLimitEachFeedFetch: Int,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (tabId != null) {
                    userGroupRepository.updateTabNotificationsPref(
                        tabId,
                        enableNotifications,
                        allowNotificationUpdates,
                        sortRecommendedTopicsBy,
                        numberOfTopicsLimitEachFeedFetch
                    )
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("groupId") groupId: String,
            @Assisted("tabId") tabId: String?,
        ): GroupTabViewModel
    }
}