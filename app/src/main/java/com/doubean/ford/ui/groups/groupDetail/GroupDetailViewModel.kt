package com.doubean.ford.ui.groups.groupDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupUserDataRepository
import com.doubean.ford.util.Event

class GroupDetailViewModel(
    groupRepository: GroupRepository,
    private val groupUserDataRepository: GroupUserDataRepository,
    private val groupId: String,
) : ViewModel() {

    private val _pagerPreselectedEvent = MutableLiveData(Event(Unit))
    val pagerPreselectedEvent: LiveData<Event<Unit>> = _pagerPreselectedEvent

    val group = groupRepository.getGroup(groupId, true)

    fun addFollow() {
        groupUserDataRepository.addFollowedGroup(groupId)
    }

    fun removeFollow() {
        groupUserDataRepository.removeFollowedGroup(groupId)
    }

    companion object {
        class Factory(
            private val repository: GroupRepository,
            private val groupUserDataRepository: GroupUserDataRepository,
            private val groupId: String,
            private val defaultTab: String?,
        ) : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GroupDetailViewModel(
                    repository,
                    groupUserDataRepository,
                    groupId
                ) as T
            }

        }
    }
}