package com.doubean.ford.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.doubean.ford.model.Resource
import com.doubean.ford.model.Status

abstract class NextPageHandler : Observer<Resource<Boolean>?> {
    private var nextPageLiveData: LiveData<Resource<Boolean>?>? = null
    val loadMoreState = MutableLiveData<LoadMoreState>()
    var hasMore = false
    private var listId: Array<out Any?>? = null

    init {
        reset()
    }

    fun loadNextPage(vararg listId: Any?) {
        if (this.listId.contentEquals(listId)) {
            return
        }
        this.listId = listId
        unregister()
        nextPageLiveData = loadNextPageFromRepo()
        loadMoreState.value = LoadMoreState(true, null)
        nextPageLiveData?.observeForever(this)
    }

    override fun onChanged(result: Resource<Boolean>?) {
        if (result == null) {
            reset()
        } else {
            when (result.status) {
                Status.SUCCESS -> {
                    hasMore = result.data == true
                    unregister()
                    loadMoreState.setValue(LoadMoreState(false, null))
                }
                Status.ERROR -> {
                    hasMore = true
                    unregister()
                    loadMoreState.setValue(
                        LoadMoreState(
                            false,
                            result.message
                        )
                    )
                }
                Status.LOADING -> {
                    // ignore
                }
            }
        }
    }

    private fun unregister() {
        nextPageLiveData?.removeObserver(this)
            nextPageLiveData = null
        if (hasMore) {
            listId = null
        }
    }

    fun reset() {
        unregister()
        hasMore = true
        loadMoreState.value = LoadMoreState(false, null)
    }

    abstract fun loadNextPageFromRepo(): LiveData<Resource<Boolean>?>

}