package com.doubean.ford.data.repository

import androidx.room.withTransaction
import com.doubean.ford.api.ApiResponse
import com.doubean.ford.api.DoubanService
import com.doubean.ford.api.model.*
import com.doubean.ford.data.db.AppDatabase
import com.doubean.ford.data.db.model.*
import com.doubean.ford.model.*
import com.doubean.ford.util.RESULT_COMMENTS_COUNT
import com.doubean.ford.util.RESULT_GROUPS_COUNT
import com.doubean.ford.util.RESULT_POSTS_COUNT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class GroupRepository private constructor(
    private val appDatabase: AppDatabase,
    private val doubanService: DoubanService,
) {
    private val groupDao = appDatabase.groupDao()
    private val userDao = appDatabase.userDao()

    fun getGroup(groupId: String): Flow<Resource<GroupDetail>> {
        return object : NetworkBoundResource<GroupDetail, NetworkGroupDetail>() {
            override suspend fun saveCallResult(item: NetworkGroupDetail) {
                val groupDetails = item.asPartialEntity()
                val groupTabs = item.tabs.map { it.asEntity(groupId) }
                val groupPostTags = item.postTags.map { it.asEntity(groupId) }
                appDatabase.withTransaction {
                    groupDao.upsertGroupDetail(groupDetails)
                    groupDao.insertGroupTabs(groupTabs)
                    groupDao.insertGroupPostTags(groupPostTags)
                }
            }

            override fun loadFromDb() =
                groupDao.loadGroupDetail(groupId).map { it.asExternalModel() }

            override suspend fun createCall() = doubanService.getGroup(groupId, 1)
        }.asFlow()
    }

    suspend fun searchNextPage(query: String): Resource<Boolean>? {
        val fetchNextSearchPageTask: FetchNextPageTask<GroupSearchResult, NetworkGroupSearch> =
            object :
                FetchNextPageTask<GroupSearchResult, NetworkGroupSearch>(
                    doubanService, appDatabase
                ) {
                override suspend fun getCurrentFromDb() = groupDao.getSearchResult(query)

                override suspend fun createCall(nextPageStart: Int?) = doubanService.searchGroups(
                    query, nextPageStart!!, RESULT_GROUPS_COUNT
                )

                override suspend fun mergeAndSaveCallResult(
                    current: GroupSearchResult,
                    item: NetworkGroupSearch,
                ) {
                    val ids = current.ids + item.items.map { it.group.id }
                    val merged = GroupSearchResult(
                        query,
                        ids,
                        item.total,
                        item.nextPageStart
                    )
                    val groups = item.items.map { it.group.asPartialEntity() }
                    appDatabase.withTransaction {
                        groupDao.insertGroupSearchResult(merged)
                        groupDao.upsertSearchResultGroups(groups)
                    }
                }
            }
        return fetchNextSearchPageTask.run()
    }

    fun search(query: String): Flow<Resource<List<GroupSearchResultGroupItem>>> {
        return object : NetworkBoundResource<List<GroupSearchResultGroupItem>, NetworkGroupSearch>(
        ) {
            override suspend fun saveCallResult(item: NetworkGroupSearch) {
                val groupIds = item.items.map { it.group.id }
                val groupSearchResult = GroupSearchResult(
                    query, groupIds, item.total, item.nextPageStart
                )
                val searchResultGroups = item.items.map { it.group.asPartialEntity() }
                appDatabase.withTransaction {
                    groupDao.upsertSearchResultGroups(searchResultGroups)
                    groupDao.insertGroupSearchResult(groupSearchResult)
                }
            }

            override fun shouldFetch(data: List<GroupSearchResultGroupItem>?): Boolean = true

            override fun loadFromDb(): Flow<List<GroupSearchResultGroupItem>?> {
                return groupDao.loadSearchResult(query).flatMapLatest { searchData ->
                    if (searchData == null) {
                        flowOf(null)
                    } else {
                        groupDao.loadOrderedSearchResultGroups(searchData.ids).map {
                            it.map(GroupSearchResultGroupItemPartialEntity::asExternalModel)
                        }
                    }
                }
            }

            override suspend fun createCall() =
                doubanService.searchGroups(query = query, count = RESULT_GROUPS_COUNT)
        }.asFlow()
    }

    fun getGroupPosts(
        groupId: String, postSortBy: PostSortBy,
    ): Flow<Resource<List<PostItem>>> {
        return object : NetworkBoundResource<List<PostItem>, NetworkPosts>() {
            override suspend fun saveCallResult(item: NetworkPosts) {
                val posts = (item.items.map { it.asPartialEntity(groupId) })
                val postIds = item.items.run {
                    if (postSortBy === PostSortBy.NEW)
                        sortedByDescending(NetworkPostItem::created)
                    else this
                }.map(NetworkPostItem::id)
                val postTagCrossRefs = item.items.flatMap(NetworkPostItem::tagCrossRefs)
                val groupPostsResult =
                    GroupPostsResult(groupId, postSortBy, postIds, item.total, item.nextPageStart)
                val authors = item.items.map { it.author.asEntity() }
                appDatabase.withTransaction {
                    groupDao.upsertPosts(posts)
                    groupDao.deletePostTagCrossRefsByPostIds(postIds)
                    groupDao.insertPostTagCrossRefs(postTagCrossRefs)
                    groupDao.insertGroupPostsResult(groupPostsResult)
                    userDao.insertUsers(authors)
                }
            }

            override fun shouldFetch(data: List<PostItem>?) = true

            override fun loadFromDb(): Flow<List<PostItem>?> {
                return groupDao.loadGroupPosts(
                    groupId, postSortBy
                ).flatMapLatest { findData ->
                    if (findData == null) {
                        flowOf(null)
                    } else {
                        groupDao.loadOrderedPosts(findData.ids).map {
                            it.map(PopulatedPostItem::asExternalModel)
                        }
                    }
                }
            }

            override suspend fun createCall(): ApiResponse<NetworkPosts> {
                val sortByRequestParam = getPostSortByRequestParam(postSortBy)
                return doubanService.getGroupPosts(
                    groupId = groupId,
                    sortBy = sortByRequestParam.toString(),
                    count = RESULT_POSTS_COUNT
                )
            }
        }.asFlow()
    }

    suspend fun getNextPageGroupPosts(
        groupId: String, postSortBy: PostSortBy,
    ): Resource<Boolean>? {
        val fetchNextPageTask =
            object :
                FetchNextPageTask<GroupPostsResult, NetworkPosts>(
                    doubanService, appDatabase
                ) {
                override suspend fun getCurrentFromDb(): GroupPostsResult? =
                    groupDao.getGroupPosts(groupId, postSortBy)!!

                override suspend fun createCall(nextPageStart: Int?): ApiResponse<NetworkPosts> {
                    return doubanService.getGroupPosts(
                        groupId = groupId,
                        sortBy = getPostSortByRequestParam(postSortBy).toString(),
                        start = nextPageStart!!,
                        count = RESULT_POSTS_COUNT
                    )
                }

                override suspend fun mergeAndSaveCallResult(
                    current: GroupPostsResult,
                    item: NetworkPosts,
                ) {
                    val ids = current.ids + item.items.run {
                        if (postSortBy === PostSortBy.NEW)
                            sortedByDescending(NetworkPostItem::created)
                        else this
                    }.map(NetworkPostItem::id)
                    val merged = GroupPostsResult(
                        groupId,
                        postSortBy,
                        ids,
                        item.total,
                        item.nextPageStart
                    )
                    val posts = item.items.map { it.asPartialEntity(groupId) }
                    val postTagCrossRefs =
                        item.items.flatMap(NetworkPostItem::tagCrossRefs)
                    val authors = item.items.map { it.author.asEntity() }
                    appDatabase.withTransaction {
                        groupDao.insertGroupPostsResult(merged)
                        groupDao.deletePostTagCrossRefsByPostIds(ids)
                        groupDao.insertPostTagCrossRefs(postTagCrossRefs)
                        groupDao.upsertPosts(posts)
                        userDao.insertUsers(authors)
                    }

                }
            }
        return fetchNextPageTask.run()
    }

    fun getGroupTagPosts(
        groupId: String, tagId: String, postSortBy: PostSortBy,
    ): Flow<Resource<List<PostItem>>> {
        return object : NetworkBoundResource<List<PostItem>, NetworkPosts>(
        ) {
            override suspend fun saveCallResult(item: NetworkPosts) {
                val posts = item.items.map { it.asPartialEntity(groupId) }
                val postIds = item.items.run {
                    if (postSortBy === PostSortBy.NEW)
                        sortedByDescending(NetworkPostItem::created)
                    else this
                }.map(NetworkPostItem::id)
                val postTagCrossRefs = item.items.flatMap(NetworkPostItem::tagCrossRefs)
                val groupTagPostsResult = GroupTagPostsResult(
                    groupId, tagId, postSortBy, postIds, item.total, item.nextPageStart
                )
                val authors = item.items.map { it.author.asEntity() }
                appDatabase.withTransaction {
                    groupDao.upsertPosts(posts)
                    groupDao.deletePostTagCrossRefsByPostIds(postIds)
                    groupDao.insertPostTagCrossRefs(postTagCrossRefs)
                    groupDao.insertGroupTagPostsResult(groupTagPostsResult)
                    userDao.insertUsers(authors)
                }
            }

            override fun shouldFetch(data: List<PostItem>?) = true

            override fun loadFromDb(): Flow<List<PostItem>?> {
                return groupDao.loadGroupTagPosts(
                    groupId, tagId, postSortBy
                ).flatMapLatest { findData ->
                    if (findData == null) {
                        flowOf(null)
                    } else {
                        groupDao.loadOrderedPosts(findData.ids).map {
                            it.map(
                                PopulatedPostItem::asExternalModel
                            )
                        }
                    }
                }
            }

            override suspend fun createCall(): ApiResponse<NetworkPosts> {
                return doubanService.getGroupPosts(
                    groupId = groupId,
                    tagId = tagId,
                    sortBy = getPostSortByRequestParam(postSortBy).toString(),
                    count = RESULT_POSTS_COUNT
                )
            }
        }.asFlow()
    }

    suspend fun getNextPageGroupTagPosts(
        groupId: String, tagId: String, postSortBy: PostSortBy,
    ): Resource<Boolean>? {
        val fetchNextPageTask: FetchNextPageTask<GroupTagPostsResult, NetworkPosts> =
            object :
                FetchNextPageTask<GroupTagPostsResult, NetworkPosts>(
                    doubanService, appDatabase
                ) {
                override suspend fun getCurrentFromDb() =
                    groupDao.getGroupTagPosts(groupId, tagId, postSortBy)!!

                override suspend fun createCall(nextPageStart: Int?) =
                    doubanService.getGroupPosts(
                        groupId,
                        tagId,
                        getPostSortByRequestParam(postSortBy).toString(),
                        nextPageStart!!,
                        RESULT_POSTS_COUNT,
                    )

                override suspend fun mergeAndSaveCallResult(
                    current: GroupTagPostsResult,
                    item: NetworkPosts,
                ) {
                    val ids = current.ids + item.items.run {
                        if (postSortBy === PostSortBy.NEW)
                            sortedByDescending(NetworkPostItem::created)
                        else this
                    }.map(NetworkPostItem::id)
                    val merged = GroupTagPostsResult(
                        groupId,
                        tagId,
                        postSortBy,
                        ids,
                        item.total,
                        item.nextPageStart
                    )
                    val posts = item.items.map { it.asPartialEntity(groupId) }
                    val postTagCrossRefs =
                        item.items.flatMap(NetworkPostItem::tagCrossRefs)
                    val authors = item.items.map { it.author.asEntity() }
                    appDatabase.withTransaction {
                        groupDao.insertGroupTagPostsResult(merged)
                        groupDao.deletePostTagCrossRefsByPostIds(ids)
                        groupDao.insertPostTagCrossRefs(postTagCrossRefs)
                        groupDao.upsertPosts(posts)
                        userDao.insertUsers(authors)
                    }
                }
            }
        return fetchNextPageTask.run()
    }

    fun getPost(postId: String): Flow<Resource<PostDetail>> {
        return object : NetworkBoundResource<PostDetail, NetworkPostDetail>() {
            override suspend fun saveCallResult(item: NetworkPostDetail) {
                val postDetail = item.asPartialEntity()
                val postTagCrossRefs = item.tagCrossRefs()
                val group = item.group.asPartialEntity()
                val author = item.author.asEntity()
                appDatabase.withTransaction {
                    groupDao.insertPostDetail(postDetail)
                    groupDao.deletePostTagCrossRefsByPostId(postId)
                    groupDao.insertPostTagCrossRefs(postTagCrossRefs)
                    groupDao.upsertPostGroup(group)
                    userDao.insertUser(author)
                }
            }

            override fun shouldFetch(data: PostDetail?) = true

            override fun loadFromDb() =
                groupDao.loadPost(postId).map(PopulatedPostDetail::asExternalModel)

            override suspend fun createCall() =
                doubanService.getGroupPost(postId)
        }.asFlow()
    }

    fun getPostComments(postId: String): Flow<Resource<PostComments>> {
        return object : NetworkBoundResource<PostComments, NetworkPostComments>() {
            override suspend fun saveCallResult(item: NetworkPostComments) {
                val topIds = item.topComments.map(NetworkPostComment::id)
                val allIds = item.allComments.map(NetworkPostComment::id)
                val commentsResult = PostCommentsResult(
                    postId, topIds, allIds, item.total, item.nextPageStart
                )
                val allComments = item.allComments.map { it.asEntity(postId) }
                val topComments = item.topComments.map { it.asEntity(postId) }
                val repliedToComments = (item.items + item.topComments).mapNotNull(
                    NetworkPostComment::repliedTo
                )
                    .map { it.asEntity(postId) }
                val comments = (allComments + topComments + repliedToComments).distinctBy(
                    PostCommentEntity::id
                )
                val allCommentsAuthors = item.items.map { it.author.asEntity() }
                val topCommentsAuthors = item.topComments.map { it.author.asEntity() }
                val repliedToAuthors = (item.items + item.topComments).mapNotNull { it.repliedTo }
                    .map { it.author.asEntity() }
                val authors =
                    (allCommentsAuthors + topCommentsAuthors + repliedToAuthors).distinctBy(
                        UserEntity::id
                    )
                appDatabase.withTransaction {
                    groupDao.insertPostComments(comments)
                    groupDao.insertPostCommentsResult(commentsResult)
                    userDao.insertUsers(authors)
                }
            }

            override fun shouldFetch(data: PostComments?) = true

            override fun loadFromDb() = groupDao.loadPostComments(postId).flatMapLatest { data ->
                if (data == null) {
                    flowOf(null)
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

            override suspend fun createCall() =
                doubanService.getPostComments(postId = postId, count = RESULT_COMMENTS_COUNT)
        }.asFlow()
    }

    suspend fun getNextPagePostComments(postId: String): Resource<Boolean>? {
        val fetchNextPageTask: FetchNextPageTask<PostCommentsResult, NetworkPostComments> =
            object :
                FetchNextPageTask<PostCommentsResult, NetworkPostComments>(
                    doubanService, appDatabase
                ) {
                override suspend fun getCurrentFromDb() = groupDao.findPostComments(postId)

                override suspend fun createCall(nextPageStart: Int?) =
                    doubanService.getPostComments(
                        postId, nextPageStart!!, RESULT_COMMENTS_COUNT
                    )

                override suspend fun mergeAndSaveCallResult(
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
                            NetworkPostComment::repliedTo
                        )
                            .map { it.asEntity(postId) }
                    val allCommentsAuthors = item.items.map { it.author.asEntity() }
                    val topCommentsAuthors = item.topComments.map { it.author.asEntity() }
                    val repliedToAuthors =
                        (item.items + item.topComments).mapNotNull { it.repliedTo }
                            .map { it.author.asEntity() }
                    val authors =
                        (allCommentsAuthors + topCommentsAuthors + repliedToAuthors).distinctBy(
                            UserEntity::id
                        )
                    appDatabase.withTransaction {
                        groupDao.insertPostCommentsResult(merged)
                        groupDao.insertPostComments(allComments + topComments + repliedToComments)
                        userDao.insertUsers(authors)
                    }
                }


            }
        return fetchNextPageTask.run()
    }

    private fun getPostSortByRequestParam(postSortBy: PostSortBy): SortByRequestParam {
        return when (postSortBy) {
            PostSortBy.LAST_UPDATED, PostSortBy.NEW -> SortByRequestParam.NEW
            PostSortBy.TOP, PostSortBy.NEW_TOP -> SortByRequestParam.TOP
        }
    }

    fun getGroupRecommendation(type: GroupRecommendationType): Flow<Resource<List<RecommendedGroupItem>>> {
        return object :
            NetworkBoundResource<List<RecommendedGroupItem>, NetworkRecommendedGroups>() {
            override suspend fun saveCallResult(item: NetworkRecommendedGroups) {
                val recommendedGroupItemGroups =
                    item.items.map { it.group.asPartialEntity() }
                val recommendedGroups = item.items.mapIndexed { index, recommendedGroupApiModel ->
                    recommendedGroupApiModel.asEntity(
                        type,
                        index + 1
                    )
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
                appDatabase.withTransaction {
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

            override fun loadFromDb(): Flow<List<RecommendedGroupItem>?> {
                return groupDao.loadRecommendedGroups(type).flatMapLatest { data ->
                    if (data == null) {
                        flowOf(null)
                    } else {
                        groupDao.loadRecommendedGroups(data.ids).map {
                            it.map(PopulatedRecommendedGroup::asExternalModel)
                        }
                    }
                }
            }

            override suspend fun createCall() = doubanService.getGroupsOfTheDay()
        }.asFlow()
    }

    companion object {
        @Volatile
        private var instance: GroupRepository? = null

        fun getInstance(
            appDatabase: AppDatabase, doubanService: DoubanService,
        ): GroupRepository? {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = GroupRepository(appDatabase, doubanService)
                    }
                }
            }
            return instance
        }
    }

}




