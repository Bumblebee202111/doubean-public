package com.doubean.ford.ui.groups.groupDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository
import com.doubean.ford.data.vo.GroupDetail
import com.doubean.ford.data.vo.Resource

class GroupDetailViewModel(
    groupRepository: GroupRepository,
    private val groupsFollowsAndSavesRepository: GroupsFollowsAndSavesRepository,
    private val groupId: String
) : ViewModel() {
    val group: LiveData<Resource<GroupDetail>> = groupRepository.getGroup(groupId, true)
    val followed: LiveData<Boolean> = groupsFollowsAndSavesRepository.getFollowed(groupId, null)

    fun addFollow() {
        groupsFollowsAndSavesRepository.createFollow(groupId, null)
    }

    fun removeFollow() {
        groupsFollowsAndSavesRepository.removeFollow(groupId, null)
    }
}