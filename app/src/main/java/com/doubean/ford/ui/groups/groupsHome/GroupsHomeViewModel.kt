package com.doubean.ford.ui.groups.groupsHome

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository
import com.doubean.ford.data.vo.*
import com.doubean.ford.util.LiveDataUtil

class GroupsHomeViewModel(
    groupRepository: GroupRepository,
    private val groupsFollowsAndSavesRepository: GroupsFollowsAndSavesRepository
) : ViewModel() {
    private val groupRepository: GroupRepository
    val followList: LiveData<List<GroupFollowItem>>

    init {
        followList = createFollowListLiveData()
        this.groupRepository = groupRepository
    }

    private fun createFollowListLiveData(): LiveData<List<GroupFollowItem>> {
        return Transformations.switchMap(
            groupsFollowsAndSavesRepository.followIds
        ) { follows: List<GroupFollow> ->
            val array: Array<LiveData<GroupFollowItem>> = Array(follows.size) {
                val group = groupRepository.getGroup(follows[it].groupId, false)
                Transformations.map(group) { g: Resource<GroupDetail?>? ->
                        GroupFollowItem(
                            follows[it].groupId,
                            follows[it].groupTabId,
                            g?.data
                        )

                }
            }

            LiveDataUtil.zip(*array)
        }
    }

    val groupsOfTheDay: LiveData<Resource<List<RecommendedGroup>>>
        get() = groupRepository.getGroupRecommendation(GroupRecommendationType.DAILY)
}