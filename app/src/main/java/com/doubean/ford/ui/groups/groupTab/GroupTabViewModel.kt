package com.doubean.ford.ui.groups.groupTab

import androidx.lifecycle.*
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupUserDataRepository
import com.doubean.ford.model.*
import com.doubean.ford.ui.common.LoadMoreState
import com.doubean.ford.ui.common.NextPageHandler

/**
 * Make LiveData refreshable:
 * https://gist.github.com/ivanalvarado/726a6c3f5ffad54958fe4670269bd897
 */
class GroupTabViewModel(
    private val groupRepository: GroupRepository,
    private val groupUserDataRepository: GroupUserDataRepository,
    private val groupId: String,
    private val tabId: String?,
) : ViewModel() {
    private val nextPageHandler = object : NextPageHandler() {
        override fun loadNextPageFromRepo(): LiveData<Resource<Boolean>?> {
            return when (tabId != null) {
                true -> groupRepository.getNextPageGroupTagPosts(
                    groupId, tabId, sortBy.value!!
                )
                else -> {
                    groupRepository.getNextPageGroupPosts(
                        groupId, sortBy.value!!
                    )
                }
            }
        }
    }
    private val reloadTrigger = MutableLiveData(Unit)
    val posts = reloadTrigger.switchMap {
        sortBy.switchMap { type ->
            if (tabId == null) groupRepository.getGroupPosts(
                groupId,
                type
            ) else groupRepository.getGroupTagPosts(groupId, tabId, type)
        }
    }
    private val sortBy = MutableLiveData<PostSortBy>()
    val group = groupRepository.getGroup(groupId, false)
    val tab = group.map { it.data?.tabs?.firstOrNull { tab -> tab.id == this.tabId } }

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
        groupUserDataRepository.addFollowedTab(tabId!!)
    }

    fun removeFollow() {
        groupUserDataRepository.removeFollowedTab(tabId!!)
    }

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    fun loadNextPage() {
        sortBy.value?.let {
            nextPageHandler.loadNextPage(it)
        }

    }

    companion object {
        class Factory(
            private val repository: GroupRepository,
            var groupUserDataRepository: GroupUserDataRepository,
            private val groupId: String,
            private val tagId: String?,
        ) : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GroupTabViewModel(repository, groupUserDataRepository, groupId, tagId) as T
            }
        }
    }
}