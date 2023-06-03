package com.doubean.ford.ui.groups.groupsHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupUserDataRepository
import com.doubean.ford.model.GroupRecommendationType

class GroupsHomeViewModel(
    groupRepository: GroupRepository,
    groupUserDataRepository: GroupUserDataRepository,
) : ViewModel() {

    val follows = groupUserDataRepository.getAllGroupFollows()

    val groupsOfTheDay = groupRepository.getGroupRecommendation(GroupRecommendationType.DAILY)

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