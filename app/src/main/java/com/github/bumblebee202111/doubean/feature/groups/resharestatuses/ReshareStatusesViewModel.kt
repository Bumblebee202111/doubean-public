package com.github.bumblebee202111.doubean.feature.groups.resharestatuses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.repository.GroupTopicRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel(assistedFactory = ReshareStatusesViewModel.Factory::class)
class ReshareStatusesViewModel @AssistedInject constructor(
    groupTopicRepository: GroupTopicRepository,
    @Assisted val topicId: String,
) : ViewModel() {
    val reshareStatusesPagingData =
        groupTopicRepository.getTopicReshareStatusesPagingData(topicId).cachedIn(viewModelScope)

    @AssistedFactory
    interface Factory {
        fun create(topicId: String): ReshareStatusesViewModel
    }
}