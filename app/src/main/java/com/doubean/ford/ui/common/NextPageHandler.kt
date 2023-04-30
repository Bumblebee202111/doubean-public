package com.doubean.ford.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.doubean.ford.data.vo.Resource
import com.doubean.ford.data.vo.Status

abstract class NextPageHandler() : Observer<Resource<Boolean>?> {
    private var nextPageLiveData: LiveData<Resource<Boolean>?>? = null
    val loadMoreState = MutableLiveData<LoadMoreState>()
    var hasMore = false
    private var params: Array<out Any?>? = null

    init {
        reset()
    }

    abstract fun loadNextPageFromRepo(params: Array<out Any?>): LiveData<Resource<Boolean>?>
    fun loadNextPage(params: Array<out Any?>) {
        if (this.params.contentEquals(params)) {
            return
        }
        this.params = params
        unregister()
        nextPageLiveData = loadNextPageFromRepo(params)
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
                else -> {}
            }
        }
    }

    private fun unregister() {
        if (nextPageLiveData != null) {
            nextPageLiveData!!.removeObserver(this)
            nextPageLiveData = null
            if (hasMore) {
                params = null
            }
        }
    }

    fun reset() {
        unregister()
        hasMore = true
        loadMoreState.value = LoadMoreState(false, null)
    }
}