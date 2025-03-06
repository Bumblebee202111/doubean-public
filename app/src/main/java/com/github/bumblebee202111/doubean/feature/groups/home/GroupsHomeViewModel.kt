@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.bumblebee202111.doubean.feature.groups.home

import androidx.lifecycle.ViewModel
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.UserGroupRepository
import com.github.bumblebee202111.doubean.data.repository.UserRepository
import com.github.bumblebee202111.doubean.model.GroupRecommendationType
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GroupsHomeViewModel @Inject constructor(
    groupRepository: GroupRepository,
    userGroupRepository: UserGroupRepository,
    authRepository: AuthRepository,
    userRepository: UserRepository,
) : ViewModel() {

    private val loggedInUserId = authRepository.observeLoggedInUserId()

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser = loggedInUserId.flatMapLatest { userId ->
        userId?.let { userRepository.getCachedUser(it) } ?: emptyFlow()
    }.stateInUi()

    val joinedGroups = authRepository.observeLoggedInUserId().flatMapLatest { userId ->
        when (userId) {
            null -> flowOf(null)
            else ->
                userGroupRepository.getUserJoinedGroups(userId).map { it.data }
        }
    }.stateInUi()

    val favorites =
        userGroupRepository.getAllGroupFavorites().flowOn(Dispatchers.IO).stateInUi()

    val groupsOfTheDay = authRepository.isLoggedIn().flatMapLatest { isLoggedIn ->
        when (isLoggedIn) {
            true -> flowOf(null)
            false -> groupRepository.getGroupRecommendation(GroupRecommendationType.DAILY)
                .map { it.data }
                .flowOn(Dispatchers.IO)
        }
    }.stateInUi()

    val recentTopicsFeed = authRepository.isLoggedIn().flatMapLatest { isLoggedIn ->
        when (isLoggedIn) {
            true -> userGroupRepository.getRecentTopicsFeed().map { it.data }
            false -> flowOf(null)
        }
    }.stateInUi()
}