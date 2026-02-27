package com.github.bumblebee202111.doubean.feature.groups.groupdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.UserGroupRepository
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.CachedAppResult
import com.github.bumblebee202111.doubean.model.groups.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = GroupDetailViewModel.Factory::class)
class GroupDetailViewModel @AssistedInject constructor(
    private val groupRepository: GroupRepository,
    private val userGroupRepository: UserGroupRepository,
    preferenceStorage: PreferenceStorage,
    private val snackBarManager: SnackbarManager,
    @Assisted("groupId") val groupId: String,
    @Assisted("initialTabId") val initialTabId: String?,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val retryTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val groupResult = retryTrigger.flatMapLatest {
        groupRepository.getGroup(groupId)
    }

    private val notificationPreferences =
        userGroupRepository.getGroupNotificationPreferences(groupId)
    private val defaultNotificationPreferences =
        preferenceStorage.defaultGroupNotificationPreferences.stateInUi()

    init {
        observeCombinedData()
    }

    private fun observeCombinedData() {
        viewModelScope.launch {
            combine(
                groupResult,
                notificationPreferences,
                defaultNotificationPreferences
            ) { groupDetailResult, notificationPreferences, defaultNotificationPreferences ->

                val uiNotificationPreferences =
                    notificationPreferences ?: defaultNotificationPreferences
                when (groupDetailResult) {
                    is CachedAppResult.Error -> {
                        val errorMessage = groupDetailResult.error.asUiMessage()
                        snackBarManager.showMessage(errorMessage)
                        GroupDetailUiState(
                            notificationPreferences = uiNotificationPreferences,
                            errorMessage = errorMessage,
                            cachedGroup = groupDetailResult.cache
                        )
                    }

                    is CachedAppResult.Loading -> {
                        GroupDetailUiState(
                            notificationPreferences = uiNotificationPreferences,
                            isLoading = true,
                            cachedGroup = groupDetailResult.cache
                        )
                    }

                    is CachedAppResult.Success -> {
                        GroupDetailUiState(
                            notificationPreferences = uiNotificationPreferences,
                            group = groupDetailResult.data
                        )

                    }
                }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun retry() {
        retryTrigger.value++
    }

    fun subscribe() {
        viewModelScope.launch {
            when (val result = userGroupRepository.subscribeGroup(groupId)) {
                is AppResult.Error -> {
                    val errorMessage = result.error.asUiMessage()
                    snackBarManager.showMessage(errorMessage)
                    _uiState.update { prevState ->
                        prevState.copy(errorMessage = errorMessage)
                    }
                }

                is AppResult.Success -> {
                    _uiState.update { prevState ->
                        prevState.copy(
                            group = prevState.group?.copy(
                                isSubscribed = true
                            )
                        )
                    }
                }
            }
        }
    }

    fun unsubscribe() {
        viewModelScope.launch {
            when (val result = userGroupRepository.unsubscribeGroup(groupId)) {
                is AppResult.Error -> {
                    val errorMessage = result.error.asUiMessage()
                    snackBarManager.showMessage(errorMessage)
                    _uiState.update { prevState ->
                        prevState.copy(errorMessage = errorMessage)
                    }
                }

                is AppResult.Success -> {
                    _uiState.update { prevState ->
                        prevState.copy(
                            group = prevState.group?.copy(
                                isSubscribed = false
                            )
                        )
                    }
                }
            }
        }
    }

    fun saveNotificationPreferences(
        preference: GroupNotificationPreferences,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userGroupRepository.updateGroupNotificationPreferences(
                    groupId = groupId,
                    preference = preference
                )
            }

        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("groupId") groupId: String,
            @Assisted("initialTabId") initialTabId: String?,
        ): GroupDetailViewModel
    }
}
