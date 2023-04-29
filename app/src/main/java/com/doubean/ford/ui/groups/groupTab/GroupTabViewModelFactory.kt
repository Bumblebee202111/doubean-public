package com.doubean.ford.ui.groups.groupTab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository

class GroupTabViewModelFactory(
    private val repository: GroupRepository,
    var groupsFollowsAndSavesRepository: GroupsFollowsAndSavesRepository,
    private val groupId: String,
    private val tagId: String?
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GroupTabViewModel(repository, groupsFollowsAndSavesRepository, groupId, tagId) as T
    }
}