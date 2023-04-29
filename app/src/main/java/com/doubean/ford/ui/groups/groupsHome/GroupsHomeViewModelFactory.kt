package com.doubean.ford.ui.groups.groupsHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository

class GroupsHomeViewModelFactory(
    private val repository: GroupRepository,
    private val groupsFollowsAndSavesRepository: GroupsFollowsAndSavesRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GroupsHomeViewModel(repository, groupsFollowsAndSavesRepository) as T
    }
}