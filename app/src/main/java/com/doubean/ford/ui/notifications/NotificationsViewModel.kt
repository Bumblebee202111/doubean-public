package com.doubean.ford.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.doubean.ford.data.repository.GroupUserDataRepository

class NotificationsViewModel(private val groupUserDataRepository: GroupUserDataRepository) :
    ViewModel() {
    val notifications =
        groupUserDataRepository.getRecommendedPostNotifications().cachedIn(viewModelScope)
            .asLiveData()

    companion object {
        class Factory(private val groupUserDataRepository: GroupUserDataRepository) :
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationsViewModel(groupUserDataRepository) as T
            }
        }
    }
}