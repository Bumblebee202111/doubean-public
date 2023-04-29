package com.doubean.ford.ui.groups.groupTab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository
import com.doubean.ford.data.vo.GroupDetail
import com.doubean.ford.data.vo.PostItem
import com.doubean.ford.data.vo.PostSortBy
import com.doubean.ford.data.vo.Resource
import com.doubean.ford.ui.common.LoadMoreState
import com.doubean.ford.ui.common.NextPageHandler

/**
 * Make LiveData refreshable:
 * https://gist.github.com/ivanalvarado/726a6c3f5ffad54958fe4670269bd897
 */
class GroupTabViewModel(
    groupRepository: GroupRepository,
    groupsFollowsAndSavesRepository: GroupsFollowsAndSavesRepository,
    groupId: String,
    tagId: String?
) : ViewModel() {
    val followed: LiveData<Boolean>
    private val nextPageHandler: NextPageHandler
    private val repository: GroupRepository
    private val groupId: String
    private val tagId: String?
    val posts: LiveData<Resource<List<PostItem>>>
    private val sortBy = MutableLiveData<PostSortBy?>()
    val group: LiveData<Resource<GroupDetail>>
    private val groupsFollowsAndSavesRepository: GroupsFollowsAndSavesRepository
    private val reloadTrigger = MutableLiveData<Boolean>()

    init {
        repository = groupRepository
        nextPageHandler = object : NextPageHandler() {
            override fun loadNextPageFromRepo(params:Array<out Any?>): LiveData<Resource<Boolean>?> {
                return when (params[1] as String? != null) {
                    true -> repository.getNextPageGroupTagPosts(
                        params[0] as String, params[1] as String, params[2] as PostSortBy
                    )
                    else -> {
                        repository.getNextPageGroupPosts(
                            params[0] as String, params[2] as PostSortBy
                        )
                    }
                }
            }
        }

        this.groupsFollowsAndSavesRepository = groupsFollowsAndSavesRepository
        this.groupId = groupId
        group = repository.getGroup(groupId, false)
        this.tagId = tagId
        posts = Transformations.switchMap(reloadTrigger) {
            Transformations.switchMap<PostSortBy?, Resource<List<PostItem>>>(
                sortBy
            ) { type ->
                if (tagId == null) repository.getGroupPosts(
                    groupId,
                    type!!
                ) else repository.getGroupTagPosts(groupId, tagId, type!!)
            }
        }
        followed = groupsFollowsAndSavesRepository.getFollowed(groupId, tagId)
        refreshPosts()
    }

    fun setSortBy(postSortBy: PostSortBy?) {
        if (postSortBy === sortBy.value) {
            return
        }
        nextPageHandler.reset()
        sortBy.value = postSortBy
    }

    fun refreshPosts() {
        reloadTrigger.value = true
    }

    fun addFollow() {
        groupsFollowsAndSavesRepository.createFollow(groupId, tagId)
    }

    fun removeFollow() {
        groupsFollowsAndSavesRepository.removeFollow(groupId, tagId)
    }

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    fun loadNextPage() {
        nextPageHandler.loadNextPage(arrayOf(groupId, tagId, sortBy.value!!))
    }
}