package com.doubean.ford.ui.groups.groupDetail

import androidx.lifecycle.*
import com.doubean.ford.data.prefs.DataStorePreferenceStorage
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupUserDataRepository
import com.doubean.ford.model.PostSortBy
import com.doubean.ford.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupDetailViewModel(
    groupRepository: GroupRepository,
    private val groupUserDataRepository: GroupUserDataRepository,
    private val preferenceStorage: DataStorePreferenceStorage,
    private val groupId: String,
) : ViewModel() {

    private val _pagerPreselectedEvent = MutableLiveData(Event(Unit))
    val pagerPreselectedEvent: LiveData<Event<Unit>> = _pagerPreselectedEvent

    val group = groupRepository.getGroup(groupId).flowOn(Dispatchers.IO).asLiveData()

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

    companion object {
        class Factory(
            private val repository: GroupRepository,
            private val groupUserDataRepository: GroupUserDataRepository,
            private val preferenceStorage: DataStorePreferenceStorage,
            private val groupId: String,
            private val defaultTab: String?,
        ) : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GroupDetailViewModel(
                    repository,
                    groupUserDataRepository,
                    preferenceStorage,
                    groupId
                ) as T
            }

        }
    }
}