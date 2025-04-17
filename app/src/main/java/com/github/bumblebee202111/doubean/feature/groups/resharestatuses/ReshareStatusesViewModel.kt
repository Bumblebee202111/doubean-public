package com.github.bumblebee202111.doubean.feature.groups.resharestatuses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.repository.GroupTopicRepository
import com.github.bumblebee202111.doubean.feature.groups.resharestatuses.navigation.ReshareStatusesRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReshareStatusesViewModel @Inject constructor(
    groupTopicRepository: GroupTopicRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val topicId = savedStateHandle.toRoute<ReshareStatusesRoute>().topicId
    val reshareStatusesPagingData =
        groupTopicRepository.getTopicReshareStatusesPagingData(topicId).cachedIn(viewModelScope)
}