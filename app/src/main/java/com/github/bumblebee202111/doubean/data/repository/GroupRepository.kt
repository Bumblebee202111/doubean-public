package com.github.bumblebee202111.doubean.data.repository

import androidx.room.withTransaction
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.model.GroupPostsResult
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResult
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultGroupItemPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTagPostsResult
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPostItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedRecommendedGroup
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupsResult
import com.github.bumblebee202111.doubean.data.db.model.UserEntity
import com.github.bumblebee202111.doubean.data.db.model.asExternalModel
import com.github.bumblebee202111.doubean.model.GroupDetail
import com.github.bumblebee202111.doubean.model.GroupRecommendationType
import com.github.bumblebee202111.doubean.model.GroupSearchResultGroupItem
import com.github.bumblebee202111.doubean.model.PostItem
import com.github.bumblebee202111.doubean.model.PostSortBy
import com.github.bumblebee202111.doubean.model.RecommendedGroupItem
import com.github.bumblebee202111.doubean.model.Result
import com.github.bumblebee202111.doubean.network.ApiResponse
import com.github.bumblebee202111.doubean.network.ApiRetrofitService
import com.github.bumblebee202111.doubean.network.model.NetworkGroupDetail
import com.github.bumblebee202111.doubean.network.model.NetworkGroupSearch
import com.github.bumblebee202111.doubean.network.model.NetworkPostItem
import com.github.bumblebee202111.doubean.network.model.NetworkPosts
import com.github.bumblebee202111.doubean.network.model.NetworkRecommendedGroup
import com.github.bumblebee202111.doubean.network.model.NetworkRecommendedGroupItemPost
import com.github.bumblebee202111.doubean.network.model.NetworkRecommendedGroups
import com.github.bumblebee202111.doubean.network.model.SortByRequestParam
import com.github.bumblebee202111.doubean.network.model.asEntity
import com.github.bumblebee202111.doubean.network.model.asPartialEntity
import com.github.bumblebee202111.doubean.network.model.postRefs
import com.github.bumblebee202111.doubean.network.model.tagCrossRefs
import com.github.bumblebee202111.doubean.util.RESULT_GROUPS_COUNT
import com.github.bumblebee202111.doubean.util.RESULT_POSTS_COUNT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class GroupRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val ApiRetrofitService: ApiRetrofitService,
) {
    private val groupDao = appDatabase.groupDao()
    private val userDao = appDatabase.userDao()

    fun getGroup(groupId: String): Flow<Result<GroupDetail>> {
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
                groupDao.loadGroupDetail(groupId).map { it?.asExternalModel() }

            override suspend fun createCall() = ApiRetrofitService.getGroup(groupId, 1)
        }.asFlow()
    }

    suspend fun searchNextPage(query: String): Result<Boolean>? {
        val fetchNextSearchPageTask: FetchNextPageTask<GroupSearchResult, NetworkGroupSearch> =
            object :
                FetchNextPageTask<GroupSearchResult, NetworkGroupSearch>(
                    ApiRetrofitService, appDatabase
                ) {
                override suspend fun getCurrentFromDb() = groupDao.getSearchResult(query)

                override suspend fun createCall(nextPageStart: Int?) =
                    ApiRetrofitService.searchGroups(
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

    fun search(query: String): Flow<Result<List<GroupSearchResultGroupItem>>> {
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
                ApiRetrofitService.searchGroups(query = query, count = RESULT_GROUPS_COUNT)
        }.asFlow()
    }

    fun getGroupPosts(
        groupId: String, postSortBy: PostSortBy,
    ): Flow<Result<List<PostItem>>> {
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
                return ApiRetrofitService.getGroupPosts(
                    groupId = groupId,
                    sortBy = sortByRequestParam.toString(),
                    count = RESULT_POSTS_COUNT
                )
            }
        }.asFlow()
    }

    suspend fun getNextPageGroupPosts(
        groupId: String, postSortBy: PostSortBy,
    ): Result<Boolean>? {
        val fetchNextPageTask =
            object :
                FetchNextPageTask<GroupPostsResult, NetworkPosts>(
                    ApiRetrofitService, appDatabase
                ) {
                override suspend fun getCurrentFromDb(): GroupPostsResult? =
                    groupDao.getGroupPosts(groupId, postSortBy)!!

                override suspend fun createCall(nextPageStart: Int?): ApiResponse<NetworkPosts> {
                    return ApiRetrofitService.getGroupPosts(
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
    ): Flow<Result<List<PostItem>>> {
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
                return ApiRetrofitService.getGroupPosts(
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
    ): Result<Boolean>? {
        val fetchNextPageTask: FetchNextPageTask<GroupTagPostsResult, NetworkPosts> =
            object :
                FetchNextPageTask<GroupTagPostsResult, NetworkPosts>(
                    ApiRetrofitService, appDatabase
                ) {
                override suspend fun getCurrentFromDb() =
                    groupDao.getGroupTagPosts(groupId, tagId, postSortBy)!!

                override suspend fun createCall(nextPageStart: Int?) =
                    ApiRetrofitService.getGroupPosts(
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

    private fun getPostSortByRequestParam(postSortBy: PostSortBy): SortByRequestParam {
        return when (postSortBy) {
            PostSortBy.LAST_UPDATED, PostSortBy.NEW -> SortByRequestParam.NEW
            PostSortBy.TOP, PostSortBy.NEW_TOP -> SortByRequestParam.TOP
        }
    }

    fun getGroupRecommendation(type: GroupRecommendationType): Flow<Result<List<RecommendedGroupItem>>> {
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
                    item.items.flatMap { it.posts.map(NetworkRecommendedGroupItemPost::id) }
                val postTagCrossRefs =
                    item.items.flatMap { it.posts.map(NetworkRecommendedGroupItemPost::tagCrossRefs) }
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
                    groupDao.upsertRecommendedGroupItemPosts(posts)
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

            override suspend fun createCall() = ApiRetrofitService.getGroupsOfTheDay()
        }.asFlow()
    }

}




