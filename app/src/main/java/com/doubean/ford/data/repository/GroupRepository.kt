package com.doubean.ford.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.doubean.ford.api.ApiResponse
import com.doubean.ford.api.DoubanService
import com.doubean.ford.api.model.*
import com.doubean.ford.data.db.AppDatabase
import com.doubean.ford.data.db.model.*
import com.doubean.ford.model.*
import com.doubean.ford.util.AppExecutors
import com.doubean.ford.util.LiveDataUtils.combine
import com.doubean.ford.util.RESULT_COMMENTS_COUNT
import com.doubean.ford.util.RESULT_GROUPS_COUNT
import com.doubean.ford.util.RESULT_POSTS_COUNT
import retrofit2.Call

class GroupRepository private constructor(
    private val appExecutors: AppExecutors,
    private val appDatabase: AppDatabase,
    private val doubanService: DoubanService,
) {
    private val groupDao = appDatabase.groupDao()
    private val userDao = appDatabase.userDao()

    fun getGroup(groupId: String, forceFetch: Boolean): LiveData<Resource<GroupDetail>> {
        return object : NetworkBoundResource<GroupDetail, NetworkGroupDetail>(appExecutors) {
            override fun saveCallResult(item: NetworkGroupDetail) {
                val groupDetails = item.asPartialEntity()
                val groupTabs = item.tabs.map { it.asEntity(groupId) }
                val groupPostTags = item.postTags.map { it.asEntity(groupId) }
                appDatabase.runInTransaction {
                    groupDao.upsertGroupDetail(groupDetails)
                    groupDao.insertGroupTabs(groupTabs)
                    groupDao.insertGroupPostTags(groupPostTags)
                }
            }

            override fun shouldFetch(data: GroupDetail?): Boolean = data == null || forceFetch

            override fun loadFromDb() =
                groupDao.loadGroupDetail(groupId).map { it.asExternalModel() }

            override fun createCall() = doubanService.getGroup(groupId, 1)
        }.asLiveData()
    }

    fun searchNextPage(query: String): LiveData<Resource<Boolean>?> {
        val fetchNextSearchPageTask: FetchNextPageTask<GroupSearchResult, NetworkGroupSearch> =
            object :
                FetchNextPageTask<GroupSearchResult, NetworkGroupSearch>(
                    doubanService, appDatabase
                ) {
                override fun loadCurrentFromDb() = groupDao.findSearchResult(query)

                override fun createCall(nextPageStart: Int?) = doubanService.searchGroups(
                    query, RESULT_GROUPS_COUNT, nextPageStart!!
                )

                override fun mergeAndSaveCallResult(
                    current: GroupSearchResult,
                    item: NetworkGroupSearch,
                ) {
                    val ids = current.ids + item.items.map { it.group.id }
                    val merged = GroupSearchResult(query,
                        ids,
                        item.total,
                        item.nextPageStart)
                    val groups = item.items.map { it.group.asPartialEntity() }
                    appDatabase.runInTransaction {
                        groupDao.insertGroupSearchResult(merged)
                        groupDao.upsertSearchResultGroups(groups)
                    }
                }
            }
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }

    fun search(query: String): LiveData<Resource<List<GroupSearchResultGroupItem>>> {
        return object : NetworkBoundResource<List<GroupSearchResultGroupItem>, NetworkGroupSearch>(
            appExecutors
        ) {
            override fun saveCallResult(item: NetworkGroupSearch) {
                val groupIds = item.items.map { it.group.id }
                val groupSearchResult = GroupSearchResult(
                    query, groupIds, item.total, item.nextPageStart
                )
                val searchResultGroups = item.items.map { it.group.asPartialEntity() }
                appDatabase.runInTransaction {
                    groupDao.upsertSearchResultGroups(searchResultGroups)
                    groupDao.insertGroupSearchResult(groupSearchResult)
                }
            }

            override fun shouldFetch(data: List<GroupSearchResultGroupItem>?): Boolean = true

            override fun loadFromDb(): LiveData<List<GroupSearchResultGroupItem>> {
                return groupDao.search(query).switchMap { searchData ->
                    if (searchData == null) {
                        object : LiveData<List<GroupSearchResultGroupItem>>(null) {}
                    } else {
                        groupDao.loadOrderedSearchResultGroups(searchData.ids).map {
                            it.map(GroupSearchResultGroupItemPartialEntity::asExternalModel)
                        }
                    }
                }
            }

            override fun createCall() =
                doubanService.searchGroups(query, RESULT_GROUPS_COUNT)
        }.asLiveData()
    }

    fun getGroupPosts(
        groupId: String, postSortBy: PostSortBy,
    ): LiveData<Resource<List<PostItem>>> {
        return object : NetworkBoundResource<List<PostItem>, NetworkPosts>(
            appExecutors
        ) {
            override fun saveCallResult(item: NetworkPosts) {
                val posts = item.items.map { it.asPartialEntity(groupId) }
                val postIds = item.items.apply {
                    if (postSortBy === PostSortBy.NEW)
                        sortedByDescending(NetworkPostItem::created)
                }.map(NetworkPostItem::id)
                val postTagCrossRefs = item.items.flatMap(NetworkPostItem::tagCrossRefs)
                val groupPostsResult =
                    GroupPostsResult(groupId, postSortBy, postIds, item.total, item.nextPageStart)
                val authors = item.items.map { it.author.asEntity() }
                appDatabase.runInTransaction {
                    groupDao.upsertPosts(posts)
                    groupDao.deletePostTagCrossRefsByPostIds(postIds)
                    groupDao.insertPostTagCrossRefs(postTagCrossRefs)
                    groupDao.insertGroupPostsResult(groupPostsResult)
                    userDao.insertUsers(authors)
                }
            }

            override fun shouldFetch(data: List<PostItem>?) = true

            override fun loadFromDb(): LiveData<List<PostItem>> {
                return groupDao.getGroupPosts(
                    groupId, postSortBy
                ).switchMap { findData ->
                    if (findData == null) {
                        object : LiveData<List<PostItem>>(null) {}
                    } else {
                        groupDao.loadPosts(findData.ids).map {
                            it.map(PopulatedPostItem::asExternalModel)
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<NetworkPosts>> {
                val sortByRequestParam = getPostSortByRequestParam(postSortBy)
                return doubanService.getGroupPosts(
                    groupId, sortByRequestParam.toString(), RESULT_POSTS_COUNT
                )
            }
        }.asLiveData()
    }

    fun getNextPageGroupPosts(
        groupId: String, postSortBy: PostSortBy,
    ): LiveData<Resource<Boolean>?> {
        val fetchNextPageTask: FetchNextPageTask<GroupPostsResult, NetworkPosts> =
            object :
                FetchNextPageTask<GroupPostsResult, NetworkPosts>(
                    doubanService, appDatabase
                ) {
                override fun loadCurrentFromDb(): GroupPostsResult =
                    groupDao.findGroupPosts(groupId, postSortBy)!!

                override fun createCall(nextPageStart: Int?): Call<NetworkPosts> {
                    return doubanService.getGroupPosts(
                        groupId,
                        getPostSortByRequestParam(postSortBy).toString(),
                        nextPageStart!!,
                        RESULT_POSTS_COUNT
                    )
                }

                override fun mergeAndSaveCallResult(
                    current: GroupPostsResult,
                    item: NetworkPosts,
                ) {
                    val ids = current.ids + item.items.apply {
                        if (postSortBy === PostSortBy.NEW)
                            sortedByDescending(NetworkPostItem::created)
                    }.map(NetworkPostItem::id)
                    val merged = GroupPostsResult(groupId,
                        postSortBy,
                        ids,
                        item.total,
                        item.nextPageStart)
                    val posts = item.items.map { it.asPartialEntity(groupId) }
                    val postTagCrossRefs =
                        item.items.flatMap(NetworkPostItem::tagCrossRefs)
                    val authors = item.items.map { it.author.asEntity() }
                    appDatabase.runInTransaction {
                        groupDao.insertGroupPostsResult(merged)
                        groupDao.deletePostTagCrossRefsByPostIds(ids)
                        groupDao.insertPostTagCrossRefs(postTagCrossRefs)
                        groupDao.upsertPosts(posts)
                        userDao.insertUsers(authors)
                    }

                }
            }
        appExecutors.networkIO().execute(fetchNextPageTask)
        return fetchNextPageTask.liveData
    }

    fun getGroupTagPosts(
        groupId: String, tagId: String, postSortBy: PostSortBy,
    ): LiveData<Resource<List<PostItem>>> {
        return object : NetworkBoundResource<List<PostItem>, NetworkPosts>(
            appExecutors
        ) {
            override fun saveCallResult(item: NetworkPosts) {
                val posts = item.items.map { it.asPartialEntity(groupId) }
                val postIds = item.items.apply {
                    if (postSortBy === PostSortBy.NEW)
                        sortedByDescending(NetworkPostItem::created)
                }.map(NetworkPostItem::id)
                val postTagCrossRefs = item.items.flatMap(NetworkPostItem::tagCrossRefs)
                val groupTagPostsResult = GroupTagPostsResult(
                    groupId, tagId, postSortBy, postIds, item.total, item.nextPageStart
                )
                val authors = item.items.map { it.author.asEntity() }
                appDatabase.runInTransaction {
                    groupDao.upsertPosts(posts)
                    groupDao.deletePostTagCrossRefsByPostIds(postIds)
                    groupDao.insertPostTagCrossRefs(postTagCrossRefs)
                    groupDao.insertGroupTagPostsResult(groupTagPostsResult)
                    userDao.insertUsers(authors)
                }
            }

            override fun shouldFetch(data: List<PostItem>?) = true

            override fun loadFromDb(): LiveData<List<PostItem>> {
                return groupDao.getGroupTagPosts(
                    groupId, tagId, postSortBy
                ).switchMap { findData ->
                    if (findData == null) {
                        object : LiveData<List<PostItem>>(null) {}
                    } else {
                        groupDao.loadPosts(findData.ids).map {
                            it.map(
                                PopulatedPostItem::asExternalModel)
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<NetworkPosts>> {
                return doubanService.getGroupTagPosts(
                    groupId,
                    tagId,
                    getPostSortByRequestParam(postSortBy).toString(),
                    RESULT_POSTS_COUNT
                )
            }
        }.asLiveData()
    }

    fun getNextPageGroupTagPosts(
        groupId: String, tagId: String, postSortBy: PostSortBy,
    ): LiveData<Resource<Boolean>?> {
        val fetchNextPageTask: FetchNextPageTask<GroupTagPostsResult, NetworkPosts> =
            object :
                FetchNextPageTask<GroupTagPostsResult, NetworkPosts>(
                    doubanService, appDatabase
                ) {
                override fun loadCurrentFromDb() =
                    groupDao.findGroupTagPosts(groupId, tagId, postSortBy)!!

                override fun createCall(nextPageStart: Int?) = doubanService.getGroupTagPosts(
                    groupId,
                    tagId,
                    getPostSortByRequestParam(postSortBy).toString(),
                    nextPageStart!!,
                    RESULT_POSTS_COUNT,
                )

                override fun mergeAndSaveCallResult(
                    current: GroupTagPostsResult,
                    item: NetworkPosts,
                ) {
                    val ids = current.ids + item.items.apply {
                        if (postSortBy === PostSortBy.NEW)
                            sortedByDescending(NetworkPostItem::created)
                    }.map(NetworkPostItem::id)
                    val merged = GroupTagPostsResult(
                        groupId,
                        tagId,
                        postSortBy,
                        ids,
                        item.total,
                        item.nextPageStart)
                    val posts = item.items.map { it.asPartialEntity(groupId) }
                    val postTagCrossRefs =
                        item.items.flatMap(NetworkPostItem::tagCrossRefs)
                    val authors = item.items.map { it.author.asEntity() }
                    appDatabase.runInTransaction {
                        groupDao.insertGroupTagPostsResult(merged)
                        groupDao.deletePostTagCrossRefsByPostIds(ids)
                        groupDao.insertPostTagCrossRefs(postTagCrossRefs)
                        groupDao.upsertPosts(posts)
                        userDao.insertUsers(authors)
                    }
                }
            }
        appExecutors.networkIO().execute(fetchNextPageTask)
        return fetchNextPageTask.liveData
    }

    fun getPost(postId: String): LiveData<Resource<PostDetail>> {
        return object : NetworkBoundResource<PostDetail, NetworkPostDetail>(appExecutors) {
            override fun saveCallResult(item: NetworkPostDetail) {
                val postDetail = item.asPartialEntity()
                val postTagCrossRefs = item.tagCrossRefs()
                val group = item.group.asPartialEntity()
                val author = item.author.asEntity()
                appDatabase.runInTransaction {
                    groupDao.insertPostDetail(postDetail)
                    groupDao.deletePostTagCrossRefsByPostId(postId)
                    groupDao.insertPostTagCrossRefs(postTagCrossRefs)
                    groupDao.upsertPostGroup(group)
                    userDao.insertUser(author)
                }
            }

            override fun shouldFetch(data: PostDetail?) = true

            override fun loadFromDb() = groupDao.loadPost(postId).map { it.asExternalModel() }

            override fun createCall() =
                doubanService.getGroupPost(postId)
        }.asLiveData()
    }

    fun getPostComments(postId: String): LiveData<Resource<PostComments>> {
        return object : NetworkBoundResource<PostComments, NetworkPostComments>(appExecutors) {
            override fun saveCallResult(item: NetworkPostComments) {
                val topIds = item.topComments.map(NetworkPostComment::id)
                val allIds = item.allComments.map(NetworkPostComment::id)
                val commentsResult = PostCommentsResult(
                    postId, topIds, allIds, item.total, item.nextPageStart
                )
                val allComments = item.allComments.map { it.asEntity(postId) }
                val topComments = item.topComments.map { it.asEntity(postId) }
                val repliedToComments = (item.items + item.topComments).mapNotNull(
                    NetworkPostComment::repliedTo)
                    .map { it.asEntity(postId) }
                val comments = (allComments + topComments + repliedToComments).distinctBy(
                    PostCommentEntity::id)
                val allCommentsAuthors = item.items.map { it.author.asEntity() }
                val topCommentsAuthors = item.topComments.map { it.author.asEntity() }
                val repliedToAuthors = (item.items + item.topComments).mapNotNull { it.repliedTo }
                    .map { it.author.asEntity() }
                val authors =
                    (allCommentsAuthors + topCommentsAuthors + repliedToAuthors).distinctBy(
                        UserEntity::id)
                appDatabase.runInTransaction {
                    groupDao.insertPostComments(comments)
                    groupDao.insertPostCommentsResult(commentsResult)
                    userDao.insertUsers(authors)
                }
            }

            override fun shouldFetch(data: PostComments?) = true

            override fun loadFromDb(): LiveData<PostComments> {
                return groupDao.getPostComments(postId).switchMap { data ->
                    if (data == null) {
                        object : LiveData<PostComments>(null) {}
                    } else {
                        val allComments = groupDao.loadOrderedComments(data.allIds)
                            .map { it.map(PopulatedPostComment::asExternalModel) }
                        val topComments = groupDao.loadOrderedComments(data.topIds).map {
                            it.map(PopulatedPostComment::asExternalModel)
                        }
                        topComments.combine(allComments) { t, a ->
                            PostComments(t, a)
                        }
                    }
                }
            }

            override fun createCall() =
                doubanService.getPostComments(postId, RESULT_COMMENTS_COUNT)
        }.asLiveData()
    }

    fun getNextPagePostComments(postId: String): LiveData<Resource<Boolean>?> {
        val fetchNextPageTask: FetchNextPageTask<PostCommentsResult, NetworkPostComments> =
            object :
                FetchNextPageTask<PostCommentsResult, NetworkPostComments>(
                    doubanService, appDatabase
                ) {
                override fun loadCurrentFromDb() = groupDao.findPostComments(postId)

                override fun createCall(nextPageStart: Int?) =
                    doubanService.getPostComments(
                        postId, nextPageStart!!, RESULT_COMMENTS_COUNT
                    )

                override fun mergeAndSaveCallResult(
                    current: PostCommentsResult,
                    item: NetworkPostComments,
                ) {
                    val topCommentIds =
                        current.topIds + item.topComments.map(NetworkPostComment::id)
                    val allCommentIds =
                        current.allIds + item.items.map(NetworkPostComment::id)
                    val merged = PostCommentsResult(
                        postId,
                        topCommentIds,
                        allCommentIds,
                        item.total,
                        item.nextPageStart
                    )
                    val allComments = item.items.map { it.asEntity(postId) }
                    val topComments = item.topComments.map { it.asEntity(postId) }
                    val repliedToComments =
                        (item.items + item.topComments).mapNotNull(
                            NetworkPostComment::repliedTo)
                            .map { it.asEntity(postId) }
                    val allCommentsAuthors = item.items.map { it.author.asEntity() }
                    val topCommentsAuthors = item.topComments.map { it.author.asEntity() }
                    val repliedToAuthors =
                        (item.items + item.topComments).mapNotNull { it.repliedTo }
                            .map { it.author.asEntity() }
                    val authors =
                        (allCommentsAuthors + topCommentsAuthors + repliedToAuthors).distinctBy(
                            UserEntity::id)
                    appDatabase.runInTransaction {
                        groupDao.insertPostCommentsResult(merged)
                        groupDao.insertPostComments(allComments + topComments + repliedToComments)
                        userDao.insertUsers(authors)
                    }
                }


            }
        appExecutors.networkIO().execute(fetchNextPageTask)
        return fetchNextPageTask.liveData
    }

    private fun getPostSortByRequestParam(postSortBy: PostSortBy): SortByRequestParam {
        return when (postSortBy) {
            PostSortBy.LAST_UPDATED, PostSortBy.NEW -> SortByRequestParam.NEW
            PostSortBy.TOP -> SortByRequestParam.TOP
        }
    }

    fun getGroupRecommendation(type: GroupRecommendationType): LiveData<Resource<List<RecommendedGroupItem>>> {
        return object :
            NetworkBoundResource<List<RecommendedGroupItem>, NetworkRecommendedGroups>(
                appExecutors
            ) {
            override fun saveCallResult(item: NetworkRecommendedGroups) {
                val recommendedGroupItemGroups =
                    item.items.map { it.group.asPartialEntity() }
                val recommendedGroups = item.items.mapIndexed { index, recommendedGroupApiModel ->
                    recommendedGroupApiModel.asEntity(type,
                        index + 1)
                }
                val posts = item.items.flatMap { g ->
                    g.posts.map { p -> p.asPartialEntity(g.group.id) }
                }
                val postIds =
                    item.items.flatMap { it.posts.map(NetworkPostItem::id) }
                val postTagCrossRefs =
                    item.items.flatMap { it.posts.map(NetworkPostItem::tagCrossRefs) }
                        .flatten()
                val postsAuthors =
                    item.items.flatMap { g -> g.posts.map { p -> p.author.asEntity() } }
                        .distinctBy(UserEntity::id)
                val recommendedGroupsResult = RecommendedGroupsResult(
                    type, item.items.map { it.group.id }
                )
                val recommendedGroupPostRefs =
                    item.items.flatMap(NetworkRecommendedGroup::postRefs)
                appDatabase.runInTransaction {
                    groupDao.upsertRecommendedGroupItemGroups(recommendedGroupItemGroups)
                    groupDao.upsertRecommendedGroups(recommendedGroups)
                    groupDao.upsertPosts(posts)
                    groupDao.deletePostTagCrossRefsByPostIds(postIds)
                    groupDao.insertPostTagCrossRefs(postTagCrossRefs)
                    userDao.insertUsers(postsAuthors)
                    groupDao.insertRecommendedGroupsResult(recommendedGroupsResult)
                    groupDao.insertRecommendedGroupPosts(recommendedGroupPostRefs)
                }
            }


            override fun shouldFetch(data: List<RecommendedGroupItem>?) = true

            override fun loadFromDb(): LiveData<List<RecommendedGroupItem>> {
                return groupDao.getRecommendedGroups(type).switchMap { data ->
                    if (data == null) {
                        object : LiveData<List<RecommendedGroupItem>>(null) {}
                    } else {
                        groupDao.loadRecommendedGroupsByIds(data.ids).map {
                            it.map(PopulatedRecommendedGroup::asExternalModel)
                        }
                    }
                }
            }

            override fun createCall() = doubanService.getGroupsOfTheDay()
        }.asLiveData()
    }


    companion object {
        @Volatile
        private var instance: GroupRepository? = null

        fun getInstance(
            appExecutors: AppExecutors, appDatabase: AppDatabase, doubanService: DoubanService,
        ): GroupRepository? {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = GroupRepository(appExecutors, appDatabase, doubanService)
                    }
                }
            }
            return instance
        }
    }

}




