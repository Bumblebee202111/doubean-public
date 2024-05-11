package com.github.bumblebee202111.doubean.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import androidx.room.withTransaction
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.COLUMN_VALUE_GROUP_TAG_ID_ALL
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultGroupItemPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPostItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedRecommendedGroup
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupsResult
import com.github.bumblebee202111.doubean.data.db.model.UserEntity
import com.github.bumblebee202111.doubean.data.db.model.asExternalModel
import com.github.bumblebee202111.doubean.data.paging.GroupSearchResultItemRemoteMediator
import com.github.bumblebee202111.doubean.data.paging.GroupTagTopicRemoteMediator
import com.github.bumblebee202111.doubean.model.GroupDetail
import com.github.bumblebee202111.doubean.model.GroupRecommendationType
import com.github.bumblebee202111.doubean.model.PostSortBy
import com.github.bumblebee202111.doubean.model.RecommendedGroupItem
import com.github.bumblebee202111.doubean.model.Result
import com.github.bumblebee202111.doubean.network.ApiKtorService
import com.github.bumblebee202111.doubean.network.ApiRetrofitService
import com.github.bumblebee202111.doubean.network.model.NetworkGroupDetail
import com.github.bumblebee202111.doubean.network.model.NetworkRecommendedGroup
import com.github.bumblebee202111.doubean.network.model.NetworkRecommendedGroupItemPost
import com.github.bumblebee202111.doubean.network.model.NetworkRecommendedGroups
import com.github.bumblebee202111.doubean.network.model.asEntity
import com.github.bumblebee202111.doubean.network.model.asPartialEntity
import com.github.bumblebee202111.doubean.network.model.postRefs
import com.github.bumblebee202111.doubean.network.model.tagCrossRefs
import com.github.bumblebee202111.doubean.util.RESULT_GROUPS_COUNT
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
    private val ApiKtorService: ApiKtorService,
) {
    private val groupDao = appDatabase.groupDao()
    private val groupTopicDao = appDatabase.groupTopicDao()
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

    @OptIn(ExperimentalPagingApi::class)
    fun search(query: String) = Pager(
        PagingConfig(
            pageSize = RESULT_GROUPS_COUNT,
            prefetchDistance = RESULT_GROUPS_COUNT / 2,
            initialLoadSize = RESULT_GROUPS_COUNT
        ),
        remoteMediator = GroupSearchResultItemRemoteMediator(
            query = query, service = ApiKtorService, appDatabase = appDatabase
        ),
        pagingSourceFactory = { groupDao.groupSearchResultPagingSource(query) }
    ).flow.map { it.map(GroupSearchResultGroupItemPartialEntity::asExternalModel) }

    @OptIn(ExperimentalPagingApi::class)
    fun getTopicsPagingData(groupId: String, tagId: String?, sortBy: PostSortBy) = Pager(
        PagingConfig(
            pageSize = RESULT_POSTS_PAGE_SIZE,
            prefetchDistance = RESULT_POSTS_PAGE_SIZE / 2,
            initialLoadSize = RESULT_POSTS_PAGE_SIZE
        ),
        remoteMediator = GroupTagTopicRemoteMediator(
            groupId = groupId,
            tagId = tagId,
            sortBy = sortBy, service = ApiKtorService, appDatabase = appDatabase
        ),
        pagingSourceFactory = {
            groupDao.groupTagTopicPagingSource(
                groupId = groupId,
                tagId = tagId ?: COLUMN_VALUE_GROUP_TAG_ID_ALL,
                sortBy = sortBy
            )
        }
    ).flow.map { it.map(PopulatedPostItem::asExternalModel) }

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
                    groupTopicDao.upsertRecommendedGroupItemPosts(posts)
                    groupTopicDao.deletePostTagCrossRefsByPostIds(postIds)
                    groupTopicDao.insertPostTagCrossRefs(postTagCrossRefs)
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

    companion object {
        const val RESULT_POSTS_PAGE_SIZE = 40
    }
}




