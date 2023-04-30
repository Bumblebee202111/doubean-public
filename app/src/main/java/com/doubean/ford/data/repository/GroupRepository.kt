package com.doubean.ford.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.doubean.ford.api.*
import com.doubean.ford.data.db.AppDatabase
import com.doubean.ford.data.db.GroupDao
import com.doubean.ford.data.vo.*
import com.doubean.ford.util.AppExecutors
import com.doubean.ford.util.Constants
import retrofit2.Call

class GroupRepository private constructor(
    private val appExecutors: AppExecutors, appDatabase: AppDatabase, doubanService: DoubanService
) {
    private val groupDao: GroupDao
    private val doubanService: DoubanService
    private val appDatabase: AppDatabase

    init {
        groupDao = appDatabase.groupDao()
        this.doubanService = doubanService
        this.appDatabase = appDatabase
    }

    fun getGroup(groupId: String, forceFetch: Boolean): LiveData<Resource<GroupDetail>> {
        return object : NetworkBoundResource<GroupDetail, GroupDetail>(appExecutors) {
            override fun saveCallResult(item: GroupDetail) {
                groupDao.upsertDetail(item)
            }

            override fun shouldFetch(data: GroupDetail?): Boolean {
                return data == null || forceFetch
            }

            override fun loadFromDb(): LiveData<GroupDetail> {
                return groupDao.loadDetail(groupId)
            }

            override fun createCall(): LiveData<ApiResponse<GroupDetail>> {
                return doubanService.getGroup(groupId, 1)
            }
        }.asLiveData()
    }

    fun searchNextPage(query: String): LiveData<Resource<Boolean>?> {
        val fetchNextSearchPageTask: FetchNextPageTask<SearchResultItem, GroupSearchResult, GroupSearchResponse> =
            object : FetchNextPageTask<SearchResultItem, GroupSearchResult, GroupSearchResponse>(
                doubanService, appDatabase
            ) {
                override fun loadFromDb(): GroupSearchResult {
                    return appDatabase.groupDao().findSearchResult(query)!!
                }

                override fun createCall(nextPageStart: Int?): Call<GroupSearchResponse> {
                    return doubanService.searchGroups(
                        query, Constants.RESULT_GROUPS_COUNT, nextPageStart!!
                    )
                }

                override fun saveMergedResult(
                    item: GroupSearchResult, items: List<SearchResultItem>
                ) {
                    appDatabase.groupDao().insertGroupSearchResult(item)
                    val groups: MutableList<GroupItem> = ArrayList()
                    for (i in items) {
                        groups.add(i.group)
                    }
                    appDatabase.groupDao().upsertGroups(groups)
                }

                override fun merge(
                    ids: List<String>, current: GroupSearchResult, total: Int, nextPageStart: Int?
                ): GroupSearchResult {
                    return GroupSearchResult(
                        query, ids, total, nextPageStart
                    )
                }
            }
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.getLiveData()
    }

    fun search(query: String): LiveData<Resource<List<GroupItem>>> {
        return object : NetworkBoundResource<List<GroupItem>, GroupSearchResponse>(
            appExecutors
        ) {
            override fun saveCallResult(item: GroupSearchResponse) {
                val groupIds = item.groupIds
                val groupSearchResult = GroupSearchResult(
                    query, groupIds, item.total, item.nextPageStart
                )
                val groups: MutableList<GroupItem> = ArrayList()
                for (searchResultItem in item.items) {
                    groups.add(searchResultItem.group)
                }
                appDatabase.runInTransaction {
                    groupDao.upsertGroups(groups)
                    groupDao.insertGroupSearchResult(groupSearchResult)
                }
            }

            override fun shouldFetch(data: List<GroupItem>?): Boolean {
                //return data == null;
                return true
            }

            override fun loadFromDb(): LiveData<List<GroupItem>> {
                return Transformations.switchMap(
                    groupDao.search(query)
                ) { searchData: GroupSearchResult? ->
                    if (searchData == null) {
                        return@switchMap object : LiveData<List<GroupItem>>(null) {}
                    } else {
                        return@switchMap groupDao.loadOrderedGroups(searchData.ids)
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<GroupSearchResponse>> {
                return doubanService.searchGroups(query, Constants.RESULT_GROUPS_COUNT)
            }

        }.asLiveData()
    }

    fun getGroupPosts(
        groupId: String, postSortBy: PostSortBy
    ): LiveData<Resource<List<PostItem>>> {
        return object : NetworkBoundResource<List<PostItem>, PostsResponse>(
            appExecutors
        ) {
            override fun saveCallResult(item: PostsResponse) {
                val posts = item.items
                val postIds = posts.map { it.id }
                val groupPostsResult =
                    GroupPostsResult(groupId, postSortBy, postIds, item.total, item.nextPageStart)
                posts.forEach {
                    it.groupId = groupId
                    if (!it.postTags.isNullOrEmpty()) {
                        it.tagId = it.postTags[0].id
                    }
                }
                appDatabase.runInTransaction {
                    groupDao.upsertPosts(posts)
                    groupDao.insertGroupPostsResult(groupPostsResult)
                }
            }

            override fun shouldFetch(data: List<PostItem>?): Boolean {
                //return data==null||data.isEmpty();
                return true
            }

            override fun loadFromDb(): LiveData<List<PostItem>> {
                return Transformations.switchMap(
                    groupDao.getGroupPosts(
                        groupId, postSortBy
                    )
                ) { findData: GroupPostsResult? ->
                    if (findData == null) {
                        return@switchMap object : LiveData<List<PostItem>?>(null) {}
                    } else {
                        return@switchMap groupDao.loadPosts(findData.ids)
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<PostsResponse>> {
                val sortByRequestParam = getPostSortByRequestParam(postSortBy)
                return doubanService.getGroupPosts(
                    groupId, sortByRequestParam.toString(), Constants.RESULT_POSTS_COUNT
                )
            }

            override fun processResponse(response: ApiResponse<PostsResponse>): PostsResponse {
                return processPostsResponse(postSortBy, response)
            }
        }.asLiveData()
    }

    fun getNextPageGroupPosts(
        groupId: String, postSortBy: PostSortBy
    ): LiveData<Resource<Boolean>?> {
        val fetchNextPageTask: FetchNextPageTask<PostItem, GroupPostsResult, PostsResponse> =
            object : FetchNextPageTask<PostItem, GroupPostsResult, PostsResponse>(
                doubanService, appDatabase
            ) {
                override fun loadFromDb(): GroupPostsResult {
                    return appDatabase.groupDao().findGroupPosts(groupId, postSortBy)!!
                }

                override fun createCall(nextPageStart: Int?): Call<PostsResponse> {
                    return doubanService.getGroupPosts(
                        groupId,
                        getPostSortByRequestParam(postSortBy).toString(),
                        Constants.RESULT_POSTS_COUNT,
                        nextPageStart!!
                    )
                }

                override fun saveMergedResult(item: GroupPostsResult, items: List<PostItem>) {
                    appDatabase.groupDao().insertGroupPostsResult(item)
                    appDatabase.groupDao().upsertPosts(items)
                }

                override fun merge(
                    ids: List<String>, current: GroupPostsResult, total: Int, nextPageStart: Int?
                ): GroupPostsResult {
                    return GroupPostsResult(groupId, postSortBy, ids, total, nextPageStart)
                }

                override fun processResponse(response: ApiResponse<PostsResponse>): PostsResponse {
                    return processPostsResponse(postSortBy, response)
                }
            }
        appExecutors.networkIO().execute(fetchNextPageTask)
        return fetchNextPageTask.getLiveData()
    }

    fun getGroupTagPosts(
        groupId: String, tagId: String, postSortBy: PostSortBy
    ): LiveData<Resource<List<PostItem>>> {
        return object : NetworkBoundResource<List<PostItem>, PostsResponse>(
            appExecutors
        ) {
            override fun saveCallResult(item: PostsResponse) {
                val posts = item.items
                val postIds = posts.map { it.id }

                val groupTagPostsResult = GroupTagPostsResult(
                    groupId, tagId, postSortBy, postIds, item.total, item.nextPageStart
                )
                for (post in posts) {
                    post.groupId = groupId
                    if (post.postTags != null && post.postTags.isNotEmpty()) {
                        post.tagId = post.postTags[0].id
                    }
                }
                appDatabase.runInTransaction {
                    groupDao.upsertPosts(posts)
                    groupDao.insertGroupTagPostsResult(groupTagPostsResult)
                }
            }

            override fun shouldFetch(data: List<PostItem>?): Boolean {
                //return data==null||data.isEmpty();
                return true
            }

            override fun loadFromDb(): LiveData<List<PostItem>> {
                return Transformations.switchMap(
                    appDatabase.groupDao().getGroupTagPosts(
                        groupId, tagId, postSortBy
                    )
                ) { findData: GroupTagPostsResult? ->
                    if (findData == null) {
                        return@switchMap object : LiveData<List<PostItem>>(null) {}
                    } else {
                        return@switchMap appDatabase.groupDao().loadPosts(findData.ids)
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<PostsResponse>> {
                return doubanService.getGroupTagPosts(
                    groupId,
                    tagId,
                    getPostSortByRequestParam(postSortBy).toString(),
                    Constants.RESULT_POSTS_COUNT
                )
            }

            override fun processResponse(response: ApiResponse<PostsResponse>): PostsResponse {
                return processPostsResponse(postSortBy, response)
            }
        }.asLiveData()
    }

    fun getNextPageGroupTagPosts(
        groupId: String, tagId: String, postSortBy: PostSortBy
    ): LiveData<Resource<Boolean>?> {
        val fetchNextPageTask: FetchNextPageTask<PostItem, GroupTagPostsResult, PostsResponse> =
            object : FetchNextPageTask<PostItem, GroupTagPostsResult, PostsResponse>(
                doubanService, appDatabase
            ) {
                override fun loadFromDb(): GroupTagPostsResult {
                    return appDatabase.groupDao().findGroupTagPosts(groupId, tagId, postSortBy)!!
                }

                override fun createCall(nextPageStart: Int?): Call<PostsResponse> {
                    return doubanService.getGroupTagPosts(
                        groupId,
                        tagId,
                        getPostSortByRequestParam(postSortBy).toString(),
                        Constants.RESULT_POSTS_COUNT,
                        nextPageStart!!
                    )
                }

                override fun saveMergedResult(item: GroupTagPostsResult, items: List<PostItem>) {

                    appDatabase.groupDao().insertGroupTagPostsResult(item)
                    appDatabase.groupDao().upsertPosts(items)
                }

                override fun merge(
                    ids: List<String>, current: GroupTagPostsResult, total: Int, nextPageStart: Int?
                ): GroupTagPostsResult {
                    return GroupTagPostsResult(
                        groupId, tagId, postSortBy, ids, total, nextPageStart
                    )
                }

                override fun processResponse(response: ApiResponse<PostsResponse>): PostsResponse {
                    return processPostsResponse(postSortBy, response)
                }
            }
        appExecutors.networkIO().execute(fetchNextPageTask)
        return fetchNextPageTask.getLiveData()
    }

    fun getPost(postId: String): LiveData<Resource<Post>> {
        return object : NetworkBoundResource<Post, Post>(appExecutors) {
            override fun saveCallResult(item: Post) {
                item.groupId = item.group!!.id
                if (!item.postTags.isNullOrEmpty()) item.tagId = item.postTags[0].id
                appDatabase.runInTransaction {
                    groupDao.insertPost(item)
                    groupDao.upsertGroupBrief(item.group)
                }
            }

            override fun shouldFetch(data: Post?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Post> {
                return groupDao.loadPost(postId)
            }

            override fun createCall(): LiveData<ApiResponse<Post>> {
                return doubanService.getGroupPost(postId)
            }
        }.asLiveData()
    }

    fun getPostComments(postId: String): LiveData<Resource<PostComments>> {
        return object : NetworkBoundResource<PostComments, PostCommentsResponse>(appExecutors) {
            override fun saveCallResult(item: PostCommentsResponse) {
                for (comment in item.items) {
                    comment.postId = postId
                }
                for (comment in item.topComments!!) {
                    comment.postId = postId
                }
                val topCommentIds = item.topCommentIds
                val topCommentsResult = PostTopComments(
                    postId, topCommentIds
                )
                val commentIds = item.items.map { it.id }
                val commentsResult = PostCommentsResult(
                    postId, commentIds, item.total, item.nextPageStart
                )
                appDatabase.runInTransaction {
                    groupDao.insertPostComments(item.items)
                    groupDao.insertPostComments(item.topComments)
                    groupDao.insertPostCommentsResult(commentsResult)
                    groupDao.insertPostTopComments(topCommentsResult)
                }
            }

            override fun shouldFetch(data: PostComments?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<PostComments> {
                val allComments = Transformations.switchMap(
                    groupDao.getPostComments(postId)
                ) { data ->
                    if (data == null) {
                        object : LiveData<List<PostComment>?>(null) {}
                    } else {
                        groupDao.loadOrderedComments(data.ids)
                    }
                }
                val topComments = Transformations.switchMap(
                    groupDao.loadPostTopComments(postId)
                ) { data ->
                    if (data == null) {
                        object : LiveData<List<PostComment>>(null) {}
                    } else {
                        groupDao.loadOrderedComments(data.commentIds)
                    }
                }
                return Transformations.switchMap(topComments) { input1: List<PostComment>? ->
                    Transformations.map(
                        allComments
                    ) { input2: List<PostComment>? -> PostComments(input1, input2) }
                }
            }

            override fun createCall(): LiveData<ApiResponse<PostCommentsResponse>> {
                return doubanService.getPostComments(postId, Constants.RESULT_POSTS_COUNT)
            }
        }.asLiveData()
    }

    fun getNextPagePostComments(postId: String): LiveData<Resource<Boolean>?> {
        val fetchNextPageTask: FetchNextPageTask<PostComment, PostCommentsResult, PostCommentsResponse> =
            object : FetchNextPageTask<PostComment, PostCommentsResult, PostCommentsResponse>(
                doubanService, appDatabase
            ) {
                override fun loadFromDb(): PostCommentsResult {
                    return appDatabase.groupDao().findPostComments(postId)!!
                }

                override fun createCall(nextPageStart: Int?): Call<PostCommentsResponse> {
                    return doubanService.getPostComments(
                        postId, nextPageStart!!, Constants.RESULT_COMMENTS_COUNT
                    )
                }

                override fun saveMergedResult(item: PostCommentsResult, items: List<PostComment>) {
                    for (comment in items) {
                        comment.postId = postId
                    }
                    appDatabase.groupDao().insertPostCommentsResult(item)
                    appDatabase.groupDao().insertPostComments(items)
                }

                override fun merge(
                    ids: List<String>, current: PostCommentsResult, total: Int, nextPageStart: Int?
                ): PostCommentsResult {
                    return PostCommentsResult(
                        postId, ids, total, nextPageStart
                    )
                }

            }
        appExecutors.networkIO().execute(fetchNextPageTask)
        return fetchNextPageTask.getLiveData()
    }

    private fun getPostSortByRequestParam(postSortBy: PostSortBy): SortByRequestParam {
        return when (postSortBy) {
            PostSortBy.LAST_UPDATED, PostSortBy.NEW -> SortByRequestParam.NEW
            PostSortBy.TOP -> SortByRequestParam.TOP
        }
    }

    fun getGroupRecommendation(type: GroupRecommendationType): LiveData<Resource<List<RecommendedGroup>>> {
        return object : NetworkBoundResource<List<RecommendedGroup>, RecommendedGroupsResponse>(
            appExecutors
        ) {
            override fun saveCallResult(item: RecommendedGroupsResponse) {
                val groups: MutableList<GroupItem> = ArrayList()
                val posts: MutableList<PostItem> = ArrayList()
                val recommendedGroupResults: MutableList<RecommendedGroupResult?> = ArrayList()
                var no = 1
                for (groupItem in item.items) {
                    groups.add(groupItem.group)
                    posts.add(groupItem.posts[0])
                    recommendedGroupResults.add(
                        RecommendedGroupResult(
                            no++, groupItem.group.id, groupItem.posts[0].id, 0
                        )
                    )
                }
                appDatabase.runInTransaction {
                    groupDao.upsertGroups(groups)
                    groupDao.upsertPosts(posts)
                    val ids = groupDao.upsertRecommendedGroups(recommendedGroupResults)
                    val recommendedGroupsResult = RecommendedGroupsResult(
                        type, ids
                    )
                    groupDao.insertRecommendedGroupsResult(recommendedGroupsResult)
                }
            }

            override fun shouldFetch(data: List<RecommendedGroup>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<RecommendedGroup>> {
                return Transformations.switchMap(
                    groupDao.getRecommendedGroups(type)
                ) { data: RecommendedGroupsResult? ->
                    if (data == null) {
                        return@switchMap object : LiveData<List<RecommendedGroup>?>(null) {}
                    } else {
                        return@switchMap groupDao.loadRecommendedGroupsByIds(data.ids)
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<RecommendedGroupsResponse>> {
                return doubanService.getGroupsOfTheDay()
            }
        }.asLiveData()
    }

    private fun processPostsResponse(
        postSortBy: PostSortBy,
        response: ApiResponse<PostsResponse>
    ): PostsResponse {
        return with(response.body!!) {
            if (postSortBy != PostSortBy.NEW) this
            else PostsResponse(start, count, total, items.sortedByDescending { it.created })
        }
    }

    companion object {
        private var instance: GroupRepository? = null

        @JvmStatic
        fun getInstance(
            appExecutors: AppExecutors, appDatabase: AppDatabase, doubanService: DoubanService
        ): GroupRepository? {
            if (instance == null) {
                synchronized(GroupRepository::class.java) {
                    if (instance == null) {
                        instance = GroupRepository(appExecutors, appDatabase, doubanService)
                    }
                }
            }
            return instance
        }
    }

}