package com.doubean.ford.ui.groups.postDetail

import androidx.lifecycle.*
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.model.Resource
import com.doubean.ford.ui.common.NextPageHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class PostDetailViewModel(
    private val groupRepository: GroupRepository,
    private val postId: String,
) : ViewModel() {

    private val reloadTrigger = MutableLiveData(Unit)
    private val nextPageHandler = object : NextPageHandler() {
        override fun loadNextPageFromRepo(): LiveData<Resource<Boolean>?> {
            return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(groupRepository.getNextPagePostComments(postId))
            }
        }

    }
    val post = groupRepository.getPost(postId).flowOn(Dispatchers.IO).asLiveData()
    val postComments = reloadTrigger.switchMap {
        groupRepository.getPostComments(postId).flowOn(Dispatchers.IO).asLiveData()

    }

    fun refreshPostComments() {
        reloadTrigger.value = Unit
    }

    val loadMoreStatus
        get() = nextPageHandler.loadMoreState

    fun loadNextPage() {
        nextPageHandler.loadNextPage()
    }

    companion object {
        class Factory(
            private val repository: GroupRepository,
            private val postId: String,
        ) : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PostDetailViewModel(repository, postId) as T
            }
        }
    }
}