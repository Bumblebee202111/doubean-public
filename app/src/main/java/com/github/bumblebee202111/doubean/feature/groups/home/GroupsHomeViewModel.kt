@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.bumblebee202111.doubean.feature.groups.home

import androidx.lifecycle.ViewModel
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.GroupUserDataRepository
import com.github.bumblebee202111.doubean.data.repository.GroupsRepo
import com.github.bumblebee202111.doubean.model.GroupRecommendationType
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class GroupsHomeViewModel @Inject constructor(
    groupRepository: GroupRepository,
    groupUserDataRepository: GroupUserDataRepository,
    groupsRepo: GroupsRepo,
    authRepository: AuthRepository,
) : ViewModel() {

    val joinedGroups = authRepository.observeLoggedInUserId().map {
        it?.let {
            groupsRepo.getUserJoinedGroups(it).getOrNull()
        }
    }.stateInUi()


    val favorites =
        groupUserDataRepository.getAllGroupFavorites().flowOn(Dispatchers.IO).stateInUi()

    val groupsOfTheDay = authRepository.isLoggedIn().flatMapLatest {
        when (it) {
            true -> flowOf(null)
            false -> groupRepository.getGroupRecommendation(GroupRecommendationType.DAILY)
                .map { it.data }
                .flowOn(Dispatchers.IO)
        }
    }.stateInUi()

    val recentTopicsFeed = authRepository.isLoggedIn().flatMapLatest {
        when (it) {
            true -> groupUserDataRepository.getRecentTopicsFeed().map { it.data }
            false -> flowOf(null)
        }
    }.stateInUi()
}