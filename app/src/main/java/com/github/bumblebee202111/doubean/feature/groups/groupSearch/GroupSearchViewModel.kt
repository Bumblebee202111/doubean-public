package com.github.bumblebee202111.doubean.feature.groups.groupSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.model.Result
import com.github.bumblebee202111.doubean.ui.common.LoadMoreState
import com.github.bumblebee202111.doubean.ui.common.NextPageHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GroupSearchViewModel @Inject constructor(private val groupRepository: GroupRepository) :
    ViewModel() {
    private val query = MutableLiveData<String>(null)
    private val nextPageHandler = object : NextPageHandler() {
        override fun loadNextPageFromRepo(): LiveData<Result<Boolean>?> {
            return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(groupRepository.searchNextPage(query.value!!))
            }
        }
    }
    private val reloadTrigger = MutableLiveData(Unit)
    val results = reloadTrigger.switchMap {
        query.switchMap { search ->
            if (search.isNullOrBlank()) {
                flowOf(null)
            } else {
                groupRepository.search(search).flowOn(Dispatchers.IO)
            }.asLiveData()
        }
    }

    fun refreshResults() {
        reloadTrigger.value = Unit
    }

    fun setQuery(originalInput: String) {
        val input = originalInput.lowercase(Locale.getDefault()).trim()
        if (input == query.value) {
            return
        }
        nextPageHandler.reset()
        query.value = input
    }

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    fun loadNextPage() {
        query.value?.let {
            if (it.isNotBlank()) {
                nextPageHandler.loadNextPage(it)
            }
        }
    }

    fun refresh() {
        query.value?.let { query.value = it }
    }

}