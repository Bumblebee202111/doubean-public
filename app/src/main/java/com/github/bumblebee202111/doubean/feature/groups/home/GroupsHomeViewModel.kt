@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.bumblebee202111.doubean.feature.groups.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.UserGroupRepository
import com.github.bumblebee202111.doubean.domain.usecase.ObserveCurrentUserUseCase
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.CachedAppResult
import com.github.bumblebee202111.doubean.model.data
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GroupsHomeViewModel @Inject constructor(
    groupRepository: GroupRepository,
    userGroupRepository: UserGroupRepository,
    authRepository: AuthRepository,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser = observeCurrentUserUseCase().stateInUi()

    val joinedGroupsUiState = authRepository.loggedInUserId.flatMapLatest { userId ->
        when (userId) {
            null -> flowOf(JoinedGroupsUiState(isLoading = false))
            else ->
                userGroupRepository.getUserJoinedGroups(userId).map { result ->
                    when (result) {
                        is CachedAppResult.Error -> {
                            snackbarManager.showMessage(result.error.asUiMessage())
                            JoinedGroupsUiState(isLoading = false, groups = result.cache)
                        }

                        is CachedAppResult.Loading -> {
                            JoinedGroupsUiState(isLoading = false, groups = result.cache)
                        }

                        is CachedAppResult.Success -> {
                            JoinedGroupsUiState(isLoading = false, groups = result.data)
                        }
                    }
                }
        }
    }.stateInUi(JoinedGroupsUiState(isLoading = true))

    val pinnedTabs =
        userGroupRepository.getPinnedTabs().flowOn(Dispatchers.IO).stateInUi()

    private val _dayRankingUiState =
        MutableStateFlow<DayRankingUiState>(DayRankingUiState.Loading)
    val dayRankingUiState = _dayRankingUiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.isLoggedIn().onEach { isLoggedIn ->
                when (isLoggedIn) {
                    true -> _dayRankingUiState.value = DayRankingUiState.Hidden
                    false ->
                        when (val result = groupRepository.getDayRanking()) {
                            is AppResult.Error -> {
                                snackbarManager.showMessage(result.error.asUiMessage())
                                _dayRankingUiState.value = DayRankingUiState.Error(result.error)
                            }

                            is AppResult.Success -> _dayRankingUiState.value =
                                DayRankingUiState.Success(result.data)
                        }
                }
            }.collect { }
        }

    }


    val recentTopicsFeed = authRepository.isLoggedIn().flatMapLatest { isLoggedIn ->
        when (isLoggedIn) {
            true -> userGroupRepository.getRecentTopicsFeed()
                .onEach { if (it is CachedAppResult.Error) snackbarManager.showMessage(it.error.asUiMessage()) }
                .map { result ->
                    result.data
                }

            false -> flowOf(null)
        }
    }.stateInUi()
}

