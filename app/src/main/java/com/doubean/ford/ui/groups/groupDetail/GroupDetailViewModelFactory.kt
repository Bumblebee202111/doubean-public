package com.doubean.ford.ui.groups.groupDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository

class GroupDetailViewModelFactory(
    private val repository: GroupRepository,
    private val groupsFollowsAndSavesRepository: GroupsFollowsAndSavesRepository,
    private val groupId: String,
    private val defaultTab: String?
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GroupDetailViewModel(
            repository,
            groupsFollowsAndSavesRepository,
            groupId
        ) as T
    }

}