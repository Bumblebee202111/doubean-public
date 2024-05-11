package com.github.bumblebee202111.doubean.feature.groups.groupDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.GroupUserDataRepository
import com.github.bumblebee202111.doubean.model.PostSortBy
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    groupRepository: GroupRepository,
    private val groupUserDataRepository: GroupUserDataRepository,
    private val preferenceStorage: PreferenceStorage,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val groupId = GroupDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).groupId

    val group =
        groupRepository.getGroup(groupId).flowOn(Dispatchers.IO).map { it.data }.asLiveData()

    val groupStateFlow = group.asFlow().stateInUi()

    val tabs = group.map { it?.tabs }.asFlow().stateInUi()

    fun addFollow() {
        viewModelScope.launch {
            groupUserDataRepository.addFollowedGroup(
                groupId = groupId,
                enablePostNotifications = preferenceStorage.perFollowDefaultEnablePostNotifications.first(),
                allowsDuplicateNotifications = preferenceStorage.perFollowDefaultAllowDuplicateNotifications.first(),
                sortRecommendedPostsBy = preferenceStorage.perFollowDefaultSortRecommendedPostsBy.first(),
                feedRequestPostCountLimit = preferenceStorage.perFollowDefaultFeedRequestPostCountLimit.first()
            )
        }

    }

    fun removeFollow() {
        viewModelScope.launch {
            groupUserDataRepository.removeFollowedGroup(groupId)
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
                groupUserDataRepository.updateGroupNotificationsPref(
                    groupId,
                    enableNotifications,
                    allowNotificationUpdates,
                    sortRecommendedPostsBy,
                    numberOfPostsLimitEachFeedFetch
                )
            }

        }
    }
}