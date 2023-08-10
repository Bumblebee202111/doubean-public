package com.doubean.ford.ui.groups.groupTab

import androidx.lifecycle.*
import com.doubean.ford.data.prefs.DataStorePreferenceStorage
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupUserDataRepository
import com.doubean.ford.model.*
import com.doubean.ford.ui.common.NextPageHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Make LiveData refreshable:
 * https://gist.github.com/ivanalvarado/726a6c3f5ffad54958fe4670269bd897
 */
class GroupTabViewModel(
    private val groupRepository: GroupRepository,
    private val groupUserDataRepository: GroupUserDataRepository,
    private val preferenceStorage: DataStorePreferenceStorage,
    private val groupId: String,
    val tabId: String?,
) : ViewModel() {
    private val nextPageHandler = object : NextPageHandler() {
        override fun loadNextPageFromRepo(): LiveData<Resource<Boolean>?> {
            return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(
                    when (tabId != null) {
                        true -> groupRepository.getNextPageGroupTagPosts(
                            groupId, tabId, sortBy.value!!
                        )
                        else -> {
                            groupRepository.getNextPageGroupPosts(
                                groupId, sortBy.value!!
                            )
                        }
                    }
                )
            }
        }
    }
    private val reloadTrigger = MutableLiveData(Unit)
    val posts = reloadTrigger.switchMap {
        sortBy.switchMap { type ->
            val postsFlow = if (tabId == null) groupRepository.getGroupPosts(
                groupId,
                type
            )
            else groupRepository.getGroupTagPosts(groupId, tabId, type)
            postsFlow.flowOn(Dispatchers.IO).asLiveData()
        }
    }
    private val sortBy = MutableLiveData<PostSortBy>()

    fun setSortBy(postSortBy: PostSortBy) {
        if (postSortBy === sortBy.value) {
            return
        }
        nextPageHandler.reset()
        sortBy.value = postSortBy
    }

    fun refreshPosts() {
        reloadTrigger.value = Unit
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

    val loadMoreStatus
        get() = nextPageHandler.loadMoreState

    fun loadNextPage() {
        sortBy.value?.let {
            nextPageHandler.loadNextPage(it)
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

    companion object {
        class Factory(
            private val groupRepository: GroupRepository,
            var groupUserDataRepository: GroupUserDataRepository,
            private val preferenceStorage: DataStorePreferenceStorage,
            private val groupId: String,
            private val tagId: String?,
        ) : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GroupTabViewModel(
                    groupRepository,
                    groupUserDataRepository,
                    preferenceStorage,
                    groupId,
                    tagId
                ) as T
            }
        }
    }
}