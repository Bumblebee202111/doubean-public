package com.github.bumblebee202111.doubean.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.repository.GroupUserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(private val groupUserDataRepository: GroupUserDataRepository) :
    ViewModel() {
    val notifications =
        groupUserDataRepository.getRecommendedPostNotifications().cachedIn(viewModelScope)

}