package com.doubean.ford.ui.groups.groupSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.vo.GroupItem
import com.doubean.ford.data.vo.Resource
import com.doubean.ford.ui.common.LoadMoreState
import com.doubean.ford.ui.common.NextPageHandler
import java.util.*

class GroupSearchViewModel(groupRepository: GroupRepository) : ViewModel() {
    val results: LiveData<Resource<List<GroupItem>>>
    private val query = MutableLiveData<String>()
    private val groupRepository: GroupRepository
    private val nextPageHandler: NextPageHandler
    private val reloadTrigger = MutableLiveData<Boolean>()

    init {
        nextPageHandler = object : NextPageHandler() {
            override fun loadNextPageFromRepo(vararg params: Any?): LiveData<Resource<Boolean>?> {
                return groupRepository.searchNextPage(params[0]!! as String)
            }
        }
        this.groupRepository = groupRepository
        results = reloadTrigger.switchMap {
            query.switchMap { search ->
                if (search.isBlank()) {
                    MutableLiveData<Resource<List<GroupItem>>>(null)
                } else {
                    groupRepository.search(search)
                }
            }
        }
        refreshResults()
    }

    fun refreshResults() {
        reloadTrigger.value = true
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
                nextPageHandler.loadNextPage(arrayOf(it))
            }
        }
    }

    fun refresh() {
        query.value?.let { query.value = it }
    }
}