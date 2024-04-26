package com.github.bumblebee202111.doubean.feature.groups.postDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.model.Resource
import com.github.bumblebee202111.doubean.ui.common.NextPageHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val postId = PostDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).postId

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

}