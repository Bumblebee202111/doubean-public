package com.doubean.ford.ui.groups.groupSearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.doubean.ford.data.repository.GroupRepository

class GroupSearchViewModelFactory(private val groupRepository: GroupRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GroupSearchViewModel(groupRepository) as T
    }
}