package com.github.bumblebee202111.doubean.feature.groups.groupDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.coroutines.AppDispatchers
import com.github.bumblebee202111.doubean.coroutines.Dispatcher
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.GroupsRepo
import com.github.bumblebee202111.doubean.data.repository.UserGroupRepository
import com.github.bumblebee202111.doubean.model.Result
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    groupRepository: GroupRepository,
    private val groupsRepo: GroupsRepo,
    private val userGroupRepository: UserGroupRepository,
    private val preferenceStorage: PreferenceStorage,
    savedStateHandle: SavedStateHandle,
    @Dispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    val groupId = GroupDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).groupId
    val initialTabId = GroupDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).defaultTabId

    private val groupResult = groupRepository.getGroup(groupId).flowOn(ioDispatcher).stateInUi()

    val group = groupResult.map { it?.data }.stateInUi()

    val tabs =
        groupResult.filter { it is Result.Success || it is Result.Error }.map { it?.data?.tabs }
            .stateInUi()

    fun subscribe() {
        viewModelScope.launch {
            userGroupRepository.subscribeGroup(groupId)
            groupsRepo.updateCachedIsSubscribed(id = groupId, isSubscribed = true)

        }
    }

    fun unsubscribe() {
        viewModelScope.launch {
            userGroupRepository.unsubscribeGroup(groupId)
            groupsRepo.updateCachedIsSubscribed(id = groupId, isSubscribed = false)
        }
    }


    fun addFavorite() {
        viewModelScope.launch {
            userGroupRepository.addFavoriteGroup(
                groupId = groupId,
                enablePostNotifications = preferenceStorage.perFollowDefaultEnablePostNotifications.first(),
                allowsDuplicateNotifications = preferenceStorage.perFollowDefaultAllowDuplicateNotifications.first(),
                sortRecommendedPostsBy = preferenceStorage.perFollowDefaultSortRecommendedPostsBy.first(),
                feedRequestPostCountLimit = preferenceStorage.perFollowDefaultFeedRequestPostCountLimit.first()
            )
        }

    }

    fun removeFavorite() {
        viewModelScope.launch {
            userGroupRepository.removeFavoriteGroup(groupId)
        }
    }

    fun saveNotificationsPreference(
        enableNotifications: Boolean,
        allowNotificationUpdates: Boolean,
        sortRecommendedTopicsBy: TopicSortBy,
        numberOfPostsLimitEachFeedFetch: Int,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userGroupRepository.updateGroupNotificationsPref(
                    groupId,
                    enableNotifications,
                    allowNotificationUpdates,
                    sortRecommendedTopicsBy,
                    numberOfPostsLimitEachFeedFetch
                )
            }

        }
    }
}