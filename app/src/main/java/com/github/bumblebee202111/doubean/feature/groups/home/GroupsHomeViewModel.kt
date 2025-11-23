package com.github.bumblebee202111.doubean.feature.groups.home

import androidx.lifecycle.ViewModel
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.UserGroupRepository
import com.github.bumblebee202111.doubean.domain.usecase.ObserveCurrentUserUseCase
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.CachedAppResult
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

    private val joinedGroupsRetryTrigger = MutableStateFlow(0)
    val joinedGroupsUiState = combine(
        authRepository.loggedInUserId,
        joinedGroupsRetryTrigger
    ) { userId, _ -> userId }.flatMapLatest { userId ->
        when (userId) {
            null -> flowOf(JoinedGroupsUiState(isLoading = false))
            else ->
                userGroupRepository.getUserJoinedGroups(userId).map { result ->
                    when (result) {
                        is CachedAppResult.Error -> {
                            val errorMessage = result.error.asUiMessage()
                            snackbarManager.showMessage(errorMessage)
                            JoinedGroupsUiState(
                                isLoading = false,
                                groups = result.cache,
                                errorMessage = errorMessage
                            )
                        }

                        is CachedAppResult.Loading -> {
                            JoinedGroupsUiState(groups = result.cache)
                        }

                        is CachedAppResult.Success -> {
                            JoinedGroupsUiState(isLoading = false, groups = result.data)
                        }
                    }
                }
        }
    }.stateInUi(JoinedGroupsUiState())

    val pinnedTabs =
        userGroupRepository.getPinnedTabs().stateInUi()

    private val isLoggedIn: Flow<Boolean> = authRepository.isLoggedIn()

    private val dayRankingRetryTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val dayRankingUiState: StateFlow<DayRankingUiState> = combine(
        isLoggedIn,
        dayRankingRetryTrigger
    ) { isLoggedIn, _ -> isLoggedIn }.flatMapLatest { isLoggedIn ->
        if (isLoggedIn) {
            flowOf<DayRankingUiState>(DayRankingUiState.Hidden)
        } else {
            flow {
                emit(DayRankingUiState.Loading)
                when (val result = groupRepository.getDayRanking()) {
                    is AppResult.Error -> {
                        val errorMessage = result.error.asUiMessage()
                        snackbarManager.showMessage(errorMessage)
                        emit(DayRankingUiState.Error(errorMessage = errorMessage))
                    }

                    is AppResult.Success -> {
                        emit(DayRankingUiState.Success(result.data))
                    }
                }
            }
        }
    }.stateInUi(DayRankingUiState.Loading)

    private val recentTopicsFeedRetryTrigger = MutableStateFlow(0)
    val recentTopicsFeedUiState = combine(
        isLoggedIn,
        recentTopicsFeedRetryTrigger
    ) { isLoggedIn, _ -> isLoggedIn }.flatMapLatest { isLoggedIn ->
        when (isLoggedIn) {
            true -> userGroupRepository.getRecentTopicsFeed()
                .map { result ->
                    when (result) {
                        is CachedAppResult.Loading -> RecentTopicsFeedUiState(
                            isLoading = true,
                            topics = result.cache
                        )

                        is CachedAppResult.Error -> {
                            val errorMessage = result.error.asUiMessage()
                            snackbarManager.showMessage(errorMessage)
                            RecentTopicsFeedUiState(
                                isLoading = false,
                                topics = result.cache,
                                errorMessage = errorMessage
                            )
                        }

                        is CachedAppResult.Success -> RecentTopicsFeedUiState(
                            isLoading = false,
                            topics = result.data
                        )
                    }
                }

            false -> flowOf(RecentTopicsFeedUiState(isLoading = false, topics = null))
        }
    }.stateInUi(RecentTopicsFeedUiState(isLoading = true))

    fun retryJoinedGroups() {
        joinedGroupsRetryTrigger.value++
    }

    fun retryRecentTopicsFeed() {
        recentTopicsFeedRetryTrigger.value++
    }

    fun retryDayRanking() {
        dayRankingRetryTrigger.value++
    }
}

