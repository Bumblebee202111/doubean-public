package com.github.bumblebee202111.doubean.feature.groups.groupTab

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.GroupUserDataRepository
import com.github.bumblebee202111.doubean.feature.groups.groupTab.GroupTabFragment.Companion.ARG_GROUP_ID
import com.github.bumblebee202111.doubean.feature.groups.groupTab.GroupTabFragment.Companion.ARG_TAG_ID
import com.github.bumblebee202111.doubean.model.PostSortBy
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
import javax.inject.Inject

/**
 * Make LiveData refreshable:
 * https://gist.github.com/ivanalvarado/726a6c3f5ffad54958fe4670269bd897
 */
@HiltViewModel
class GroupTabViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val groupUserDataRepository: GroupUserDataRepository,
    private val preferenceStorage: PreferenceStorage,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val tabId: String? = savedStateHandle[ARG_TAG_ID]
    private val groupId: String = savedStateHandle[ARG_GROUP_ID]!!

    private val _sortBy = MutableStateFlow<PostSortBy?>(null)

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

    fun setSortBy(postSortBy: PostSortBy) {
        _sortBy.value = postSortBy
    }

    fun addFollow() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                groupUserDataRepository.addFollowedTab(
                    groupId = groupId,
                    tabId = tabId!!,
                    enablePostNotifications = preferenceStorage.perFollowDefaultEnablePostNotifications.first(),
                    allowsDuplicateNotifications = preferenceStorage.perFollowDefaultAllowDuplicateNotifications.first(),
                    sortRecommendedPostsBy = preferenceStorage.perFollowDefaultSortRecommendedPostsBy.first(),
                    feedRequestPostCountLimit = preferenceStorage.perFollowDefaultFeedRequestPostCountLimit.first()
                )
            }
        }
    }

    fun removeFollow() {
        viewModelScope.launch {
            groupUserDataRepository.removeFollowedTab(tabId!!)
        }
    }

    fun saveNotificationsPreference(
        enableNotifications: Boolean,
        allowNotificationUpdates: Boolean,
        sortRecommendedPostsBy: PostSortBy,
        numberOfPostsLimitEachFeedFetch: Int,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (tabId != null) {
                    groupUserDataRepository.updateTabNotificationsPref(
                        tabId,
                        enableNotifications,
                        allowNotificationUpdates,
                        sortRecommendedPostsBy,
                        numberOfPostsLimitEachFeedFetch
                    )
                }
            }
        }
    }
}