package com.github.bumblebee202111.doubean.feature.groups.groupdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.UserGroupRepository
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.navigation.GroupDetailRoute
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.CachedAppResult
import com.github.bumblebee202111.doubean.model.groups.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.toUiMessage
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val userGroupRepository: UserGroupRepository,
    private val preferenceStorage: PreferenceStorage,
    savedStateHandle: SavedStateHandle,
    private val snackBarManager: SnackbarManager,
) : ViewModel() {
    val groupId = savedStateHandle.toRoute<GroupDetailRoute>().groupId
    val initialTabId = savedStateHandle.toRoute<GroupDetailRoute>().defaultTabId
    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState = _uiState.asStateFlow()
    private val groupDetailResult = groupRepository.getGroup(groupId)
    private val isFavorited = userGroupRepository.getGroupFavorite(groupId)
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
                groupDetailResult,
                isFavorited,
                notificationPreferences,
                defaultNotificationPreferences
            ) { groupDetailResult, isFavorited, notificationPreferences, defaultNotificationPreferences ->

                val uiNotificationPreferences =
                    notificationPreferences ?: defaultNotificationPreferences
                when (groupDetailResult) {
                    is CachedAppResult.Error -> {
                        snackBarManager.showMessage(groupDetailResult.error.asUiMessage())
                        GroupDetailUiState(
                            isFavorited = isFavorited,
                            notificationPreferences = uiNotificationPreferences,
                            isError = true,
                            cachedGroup = groupDetailResult.cache
                        )
                    }

                    is CachedAppResult.Loading -> {
                        GroupDetailUiState(
                            isFavorited = isFavorited,
                            notificationPreferences = uiNotificationPreferences,
                            isLoading = true,
                            cachedGroup = groupDetailResult.cache
                        )
                    }

                    is CachedAppResult.Success -> {
                        GroupDetailUiState(
                            isFavorited = isFavorited,
                            notificationPreferences = uiNotificationPreferences,
                            groupDetail = groupDetailResult.data
                        )

                    }
                }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun subscribe() {
        viewModelScope.launch {
            when (val result = userGroupRepository.subscribeGroup(groupId)) {
                is AppResult.Error -> {
                    snackBarManager.showMessage(result.error.asUiMessage())
                    _uiState.update { prevState ->
                        prevState.copy(isError = true)
                    }
                }

                is AppResult.Success -> {
                    _uiState.update { prevState ->
                        prevState.copy(
                            groupDetail = prevState.groupDetail?.copy(
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
                    snackBarManager.showMessage(result.error.asUiMessage())
                    _uiState.update { prevState ->
                        prevState.copy(isError = true)
                    }
                }

                is AppResult.Success -> {
                    _uiState.update { prevState ->
                        prevState.copy(
                            groupDetail = prevState.groupDetail?.copy(
                                isSubscribed = false
                            )
                        )
                    }
                }
            }
        }
    }

    fun addFavorite() {
        viewModelScope.launch {
            userGroupRepository.addFavoriteGroup(groupId = groupId)
            snackBarManager.showMessage(R.string.favorited_group.toUiMessage())
        }

    }

    fun removeFavorite() {
        viewModelScope.launch {
            userGroupRepository.removeFavoriteGroup(groupId)
            snackBarManager.showMessage(R.string.unfavorited_group.toUiMessage())
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
}