package com.doubean.ford.ui.groups.groupsHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupUserDataRepository
import com.doubean.ford.model.GroupRecommendationType
import kotlinx.coroutines.Dispatchers

class GroupsHomeViewModel(
    groupRepository: GroupRepository,
    groupUserDataRepository: GroupUserDataRepository,
) : ViewModel() {

    val follows = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emitSource(groupUserDataRepository.getAllGroupFollows())
    }

    val groupsOfTheDay = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emitSource(groupRepository.getGroupRecommendation(GroupRecommendationType.DAILY))
    }

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