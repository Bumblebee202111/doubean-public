package com.github.bumblebee202111.doubean.feature.groups.groupdetail.tab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.UserGroupRepository
import com.github.bumblebee202111.doubean.model.groups.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.toUiMessage
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
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
    private val snackbarManager: SnackbarManager,
) : ViewModel() {

    private val _sortBy = MutableStateFlow(TopicSortBy.NEW_LAST_CREATED)
    val sortBy = _sortBy.asStateFlow()
    val defaultNotificationPreferences =
        preferenceStorage.defaultGroupNotificationPreferences

    @OptIn(ExperimentalCoroutinesApi::class)
    val topicsPagingData =
        _sortBy.flatMapLatest { sortBy ->
            groupRepository.getTopicsPagingData(
                groupId = groupId,
                tagId = tabId,
                sortBy = sortBy
            ).cachedIn(viewModelScope)
        }.cachedIn(viewModelScope)

    fun updateSortBy(topicSortBy: TopicSortBy) {
        _sortBy.value = topicSortBy
    }

    val isPinned =
        (tabId?.let { userGroupRepository.isTabPinned(it) } ?: emptyFlow()).stateInUi()

    val topicNotificationPreferences = (tabId?.let {
        combine(
            userGroupRepository.getTabNotificationPreferences(it),
            defaultNotificationPreferences
        ) { created, default ->
            return@combine created ?: default
        }
    } ?: emptyFlow()).stateInUi()

    fun pinTab() {
        val tabId = tabId ?: return
        viewModelScope.launch {
            userGroupRepository.pinTab(
                groupId = groupId,
                tabId = tabId,
            )
            snackbarManager.showMessage(R.string.tab_pinned.toUiMessage())
        }
    }

    fun unpinTab() {
        val tabId = tabId ?: return
        viewModelScope.launch {
            userGroupRepository.unpinTab(tabId)
            snackbarManager.showMessage(R.string.tab_unpinned.toUiMessage())
        }
    }

    fun saveNotificationPreferences(
        preferences: GroupNotificationPreferences,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (tabId != null) {
                    userGroupRepository.updateTabNotificationPreferences(
                        groupId = groupId,
                        tabId = tabId,
                        preference = preferences
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