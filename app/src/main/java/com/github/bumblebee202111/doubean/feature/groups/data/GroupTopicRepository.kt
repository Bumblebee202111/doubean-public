package com.github.bumblebee202111.doubean.feature.groups.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItemWithGroup
import com.github.bumblebee202111.doubean.data.db.model.asExternalModel
import com.github.bumblebee202111.doubean.data.db.model.toTopicDetail
import com.github.bumblebee202111.doubean.feature.groups.model.GroupTopicCommentReshareItem
import com.github.bumblebee202111.doubean.feature.groups.model.TopicComment
import com.github.bumblebee202111.doubean.feature.groups.model.TopicDetail
import com.github.bumblebee202111.doubean.model.CachedAppResult
import com.github.bumblebee202111.doubean.model.fangorns.ReactionType
import com.github.bumblebee202111.doubean.network.api.GroupApiService
import com.github.bumblebee202111.doubean.network.model.NetworkGroupTopicComment
import com.github.bumblebee202111.doubean.network.model.NetworkRecentTopicsFeedItem
import com.github.bumblebee202111.doubean.network.model.NetworkTopicItemWithGroup
import com.github.bumblebee202111.doubean.network.model.asExternalModel
import com.github.bumblebee202111.doubean.network.model.fangorns.toGroupTopicTagEntity
import com.github.bumblebee202111.doubean.network.model.fangorns.toNetworkReactionType
import com.github.bumblebee202111.doubean.network.model.fangorns.toTopicReactionPartialEntity
import com.github.bumblebee202111.doubean.network.model.fangorns.toUserEntity
import com.github.bumblebee202111.doubean.network.model.structure.NetworkReshareItem
import com.github.bumblebee202111.doubean.network.model.structure.toReshareItem
import com.github.bumblebee202111.doubean.network.model.tagCrossRefs
import com.github.bumblebee202111.doubean.network.model.toCachedGroupEntity
import com.github.bumblebee202111.doubean.network.model.toEntity
import com.github.bumblebee202111.doubean.network.model.toSimpleCachedGroupPartialEntity
import com.github.bumblebee202111.doubean.network.model.toTopicItemPartialEntity
import com.github.bumblebee202111.doubean.network.model.toTopicPartialEntity
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import com.github.bumblebee202111.doubean.network.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class TopicCommentsResult(
    val popularComments: StateFlow<List<TopicComment>>,
    val allCommentsPagingData: Flow<PagingData<TopicComment>>,
)
@Singleton
class GroupTopicRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val apiService: GroupApiService,
) {

    private val groupDao = appDatabase.groupDao()
    private val userGroupDao = appDatabase.userGroupDao()
    private val groupTopicDao = appDatabase.groupTopicDao()
    private val userDao = appDatabase.userDao()
    private val topicDao = appDatabase.groupTopicDao()

    fun getTopic(
        id: String,
        spmId: String? = null,
    ): Flow<CachedAppResult<TopicDetail, TopicDetail?>> =
        networkBoundResource(
            queryDb = {
                groupTopicDao.loadTopic(id)
            },
            fetchRemote = { apiService.getGroupTopic(id, spmId) },
            saveRemoteResponseToDb = { response ->
                val topicDetail = response.toTopicItemPartialEntity()
                val topicTags =
                    response.topicTags.map { it.toGroupTopicTagEntity(response.group.id) }
                val topicTagCrossRefs = response.tagCrossRefs()
                val group = response.group.toCachedGroupEntity()
                val author = response.author.toUserEntity()
                appDatabase.withTransaction {
                    groupDao.insertTopicTags(topicTags)
                    groupTopicDao.insertTopicDetail(topicDetail)
                    groupTopicDao.deleteTopicTagCrossRefsByTopicId(id)
                    groupTopicDao.insertTopicTagCrossRefs(topicTagCrossRefs)
                    groupDao.insertCachedGroup(group)
                    userDao.insertUser(author)
                }
            },
            mapDbEntityToDomain = { entity ->
                entity.toTopicDetail()
            }
        )

    fun getTopicCommentsData(
        topicId: String,
        spmId: String? = null,
        onlyOp: Boolean = false,
    ): TopicCommentsResult {
        val popularComments = MutableStateFlow<List<TopicComment>>(emptyList())
        val allCommentsPagingData = Pager(
            config = PagingConfig(
                pageSize = RESULT_COMMENTS_PAGE_SIZE,
                prefetchDistance = RESULT_COMMENTS_PAGE_SIZE / 2,
                enablePlaceholders = true,
                initialLoadSize = RESULT_COMMENTS_PAGE_SIZE,
                jumpThreshold = RESULT_COMMENTS_PAGE_SIZE * 3
            ),
            pagingSourceFactory = {
                GroupTopicCommentPagingSource(
                    apiService = apiService,
                    topicId = topicId,
                    spmId = spmId,
                    onlyOp = onlyOp,
                    onPopularCommentsFetched = {
                        popularComments.value = it.map(NetworkGroupTopicComment::asExternalModel)
                    })
            }
        ).flow.map { it.map(NetworkGroupTopicComment::asExternalModel) }
        return TopicCommentsResult(popularComments, allCommentsPagingData)
    }

    fun getTopicReshareStatusesPagingData(topicId: String): Flow<PagingData<GroupTopicCommentReshareItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = RESULT_RESHARE_STATUSES_PAGE_SIZE,
                prefetchDistance = RESULT_RESHARE_STATUSES_PAGE_SIZE / 2,
                enablePlaceholders = true,
                initialLoadSize = RESULT_RESHARE_STATUSES_PAGE_SIZE,
                jumpThreshold = RESULT_RESHARE_STATUSES_PAGE_SIZE * 2
            ),
            pagingSourceFactory = {
                GroupTopicReshareItemPagingSource(apiService, topicId)
            }
        ).flow.map { it.map(NetworkReshareItem::toReshareItem) }
    }

    suspend fun react(
        topicId: String, reactionType: ReactionType,
    ) = makeApiCall(
        apiCall = {
            apiService.reactGroupTopic(topicId, reactionType.toNetworkReactionType())
        },
        mapSuccess = {
            groupTopicDao.insertTopicReaction(it.toTopicReactionPartialEntity(topicId))
        }
    )

    suspend fun updateTopicIsCollected(topicId: String, isCollected: Boolean) {
        groupTopicDao.updateIsCollected(topicId, isCollected)
    }

    fun getRecentTopicsFeed() = networkBoundResource(
        queryDb = {
            userGroupDao.getTopicsFeed()
        },
        fetchRemote = {
            apiService.getGroupUserRecentTopicsFeed()
        },
        saveRemoteResponseToDb = { response ->
            val networkTopics = response.feeds
                .map(NetworkRecentTopicsFeedItem::topic)
                .filterIsInstance<NetworkTopicItemWithGroup>()
            val feedItemEntities = networkTopics.map(NetworkTopicItemWithGroup::toEntity)

            val topicPartialEntities = networkTopics.map { it.toTopicPartialEntity() }
            val groups = networkTopics.map { it.group.toSimpleCachedGroupPartialEntity() }
            val userEntities = networkTopics.map { it.author.toUserEntity() }
            val topicTagEntities =
                networkTopics.flatMap { topic ->
                    topic.topicTags.map {
                        it.toGroupTopicTagEntity(
                            topic.group.id
                        )
                    }
                }
                    .distinctBy { it.id }

            appDatabase.withTransaction {
                userGroupDao.apply {
                    deleteTopicsFeed()
                    insertTopicsFeed(feedItemEntities)
                }
                topicDao.upsertTopicItems(topicPartialEntities)
                userDao.insertUsers(userEntities)
                groupDao.apply {
                    upsertSimpleCachedGroups(groups)
                    insertTopicTags(topicTagEntities)
                }
            }
        },
        mapDbEntityToDomain = { entity ->
            entity.map(PopulatedTopicItemWithGroup::asExternalModel)
        }
    )

    companion object {
        const val RESULT_COMMENTS_PAGE_SIZE = 40
        const val RESULT_RESHARE_STATUSES_PAGE_SIZE = 20
    }
}
