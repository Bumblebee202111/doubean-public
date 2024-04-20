package com.github.bumblebee202111.doubean.feature.groups.groupTab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.GroupUserDataRepository
import com.github.bumblebee202111.doubean.model.PostSortBy
import com.github.bumblebee202111.doubean.model.Resource
import com.github.bumblebee202111.doubean.ui.common.NextPageHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GroupTabViewModel(
    private val groupRepository: GroupRepository,
    private val groupUserDataRepository: GroupUserDataRepository,
    private val preferenceStorage: PreferenceStorage,
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
    private val sortBy = MutableLiveData<PostSortBy>()
    val posts = reloadTrigger.switchMap { _ ->
        sortBy.switchMap { type ->
            val postsFlow = if (tabId == null) groupRepository.getGroupPosts(
                groupId,
                type
            )
            else groupRepository.getGroupTagPosts(groupId, tabId, type)
            postsFlow.flowOn(Dispatchers.IO).asLiveData()
        }
    }


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
            private val preferenceStorage: PreferenceStorage,
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