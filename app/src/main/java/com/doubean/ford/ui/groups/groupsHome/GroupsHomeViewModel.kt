package com.doubean.ford.ui.groups.groupsHome

import androidx.lifecycle.*
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupUserDataRepository
import com.doubean.ford.model.GroupRecommendationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class GroupsHomeViewModel(
    groupRepository: GroupRepository,
    groupUserDataRepository: GroupUserDataRepository,
) : ViewModel() {

    val follows = groupUserDataRepository.getAllGroupFollows().flowOn(Dispatchers.IO).asLiveData()

    val groupsOfTheDay =
        groupRepository.getGroupRecommendation(GroupRecommendationType.DAILY).flowOn(Dispatchers.IO)
            .asLiveData()

    companion object {
        class Factory(
            private val repository: GroupRepository,
            private val groupUserDataRepository: GroupUserDataRepository,
        ) : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GroupsHomeViewModel(repository, groupUserDataRepository) as T
            }
        }
    }
}