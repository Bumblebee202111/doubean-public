package com.github.bumblebee202111.doubean.feature.groups.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.model.groups.GroupRecommendationType
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GroupsSearchViewModel @Inject constructor(private val groupRepository: GroupRepository) :
    ViewModel() {
    private val query = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val results =
        query.flatMapLatest { search ->
            if (search == null) {
                flowOf(PagingData.empty())
            } else {
                groupRepository.search(search).cachedIn(viewModelScope)
            }

        }.cachedIn(viewModelScope)

    fun onQueryChange(query: String) {
        if (query.isBlank()) {
            this.query.value = null
        }
    }

    fun onSearchTriggered(originalInput: String) {
        val input = originalInput.lowercase(Locale.getDefault()).trim().takeUnless(String::isEmpty)
        query.value = input
    }

    val groupsOfTheDay = query.flatMapLatest { query ->
        if (query == null) {
            groupRepository.getGroupRecommendation(GroupRecommendationType.DAILY).map { it.data }
                .flowOn(Dispatchers.IO)
        } else {
            flowOf(null)
        }
    }.stateInUi()


}