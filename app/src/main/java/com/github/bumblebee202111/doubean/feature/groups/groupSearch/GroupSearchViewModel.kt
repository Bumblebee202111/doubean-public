package com.github.bumblebee202111.doubean.feature.groups.groupSearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GroupSearchViewModel @Inject constructor(private val groupRepository: GroupRepository) :
    ViewModel() {
    private val query = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val results =
        query.flatMapLatest { search ->
            if (search.isNullOrBlank()) {
                flowOf(PagingData.empty())
            } else {
                groupRepository.search(search).cachedIn(viewModelScope)
            }

        }.cachedIn(viewModelScope)

    fun setQuery(originalInput: String) {
        val input = originalInput.lowercase(Locale.getDefault()).trim()
        query.value = input
    }

}