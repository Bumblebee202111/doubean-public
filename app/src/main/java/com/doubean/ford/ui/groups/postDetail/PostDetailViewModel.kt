package com.doubean.ford.ui.groups.postDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.vo.Post
import com.doubean.ford.data.vo.PostComments
import com.doubean.ford.data.vo.Resource
import com.doubean.ford.ui.common.LoadMoreState
import com.doubean.ford.ui.common.NextPageHandler

class PostDetailViewModel(groupRepository: GroupRepository, postId: String) : ViewModel() {
    private val nextPageHandler: NextPageHandler
    private val reloadTrigger = MutableLiveData<Boolean>()
    private val postId: String
    private val groupRepository: GroupRepository
    val post: LiveData<Resource<Post>>
    val postComments: LiveData<Resource<PostComments>>

    init {
        nextPageHandler = object : NextPageHandler() {
            override fun loadNextPageFromRepo(params:Array<out Any?>): LiveData<Resource<Boolean>?> {
                return groupRepository.getNextPagePostComments(params[0] as String)
            }

        }
        this.groupRepository = groupRepository
        this.postId = postId
        post = groupRepository.getPost(postId)
        postComments = Transformations.switchMap(reloadTrigger) {
            groupRepository.getPostComments(postId)
        }
        refreshPostComments()
    }

    fun refreshPostComments() {
        reloadTrigger.value = true
    }

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    fun loadNextPage() {
        nextPageHandler.loadNextPage(arrayOf(postId))
    }
}