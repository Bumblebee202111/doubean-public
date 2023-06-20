package com.doubean.ford.ui.groups.groupSearch

import androidx.lifecycle.*
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.model.GroupSearchResultGroupItem
import com.doubean.ford.model.Resource
import com.doubean.ford.ui.common.LoadMoreState
import com.doubean.ford.ui.common.NextPageHandler
import kotlinx.coroutines.Dispatchers
import java.util.*

class GroupSearchViewModel(private val groupRepository: GroupRepository) : ViewModel() {
    private val query = MutableLiveData<String>()
    private val nextPageHandler = object : NextPageHandler() {
        override fun loadNextPageFromRepo(): LiveData<Resource<Boolean>?> {
            return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(groupRepository.searchNextPage(query.value!!))
            }
        }
    }
    private val reloadTrigger = MutableLiveData(Unit)
    val results = reloadTrigger.switchMap {
        query.switchMap { search ->
            if (search.isBlank()) {
                MutableLiveData<Resource<List<GroupSearchResultGroupItem>>>(null)
            } else {
                liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                    emitSource(groupRepository.search(search))
                }

            }
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

    companion object {
        class Factory(private val groupRepository: GroupRepository) :
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GroupSearchViewModel(groupRepository) as T
            }
        }
    }
}