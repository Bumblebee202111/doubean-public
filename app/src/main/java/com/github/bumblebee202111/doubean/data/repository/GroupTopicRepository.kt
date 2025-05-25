package com.github.bumblebee202111.doubean.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.model.toTopicDetail
import com.github.bumblebee202111.doubean.data.paging.GroupTopicCommentPagingSource
import com.github.bumblebee202111.doubean.data.paging.GroupTopicReshareItemPagingSource
import com.github.bumblebee202111.doubean.model.CachedAppResult
import com.github.bumblebee202111.doubean.model.fangorns.ReactionType
import com.github.bumblebee202111.doubean.model.groups.GroupTopicCommentReshareItem
import com.github.bumblebee202111.doubean.model.groups.TopicComment
import com.github.bumblebee202111.doubean.model.groups.TopicDetail
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkGroupTopicComment
import com.github.bumblebee202111.doubean.network.model.NetworkReshareItem
import com.github.bumblebee202111.doubean.network.model.asExternalModel
import com.github.bumblebee202111.doubean.network.model.fangorns.toUserEntity
import com.github.bumblebee202111.doubean.network.model.tagCrossRefs
import com.github.bumblebee202111.doubean.network.model.toCachedGroupEntity
import com.github.bumblebee202111.doubean.network.model.toGroupTopicTagEntity
import com.github.bumblebee202111.doubean.network.model.toNetworkReactionType
import com.github.bumblebee202111.doubean.network.model.toTopicItemPartialEntity
import com.github.bumblebee202111.doubean.network.model.toTopicReactionPartialEntity
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import com.github.bumblebee202111.doubean.network.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupTopicRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val apiService: ApiService,
) {

    private val groupDao = appDatabase.groupDao()
    private val groupTopicDao = appDatabase.groupTopicDao()
    private val userDao = appDatabase.userDao()

    fun getTopic(id: String): Flow<CachedAppResult<TopicDetail, TopicDetail?>> =
        networkBoundResource(
            queryDb = {
                groupTopicDao.loadTopic(id)
        },
            fetchRemote = { apiService.getGroupTopic(id) },
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

    fun getTopicCommentsData(topicId: String): Pair<StateFlow<List<TopicComment>>, Flow<PagingData<TopicComment>>> {
        val popularComments = MutableStateFlow<List<TopicComment>>(emptyList())
        val allCommentsPagingData = Pager(
            config = PagingConfig(
                pageSize = RESULT_COMMENTS_PAGE_SIZE,
                prefetchDistance = RESULT_COMMENTS_PAGE_SIZE / 2,
                enablePlaceholders = true,
                initialLoadSize = RESULT_COMMENTS_PAGE_SIZE
            ),
            pagingSourceFactory = {
                GroupTopicCommentPagingSource(apiService, topicId) {
                    popularComments.value = it.map(NetworkGroupTopicComment::asExternalModel)
                }
            }
        ).flow.map { it.map(NetworkGroupTopicComment::asExternalModel) }
        return Pair(first = popularComments, second = allCommentsPagingData)
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
        ).flow.map { it.map(NetworkReshareItem::asExternalModel) }
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

    companion object {
        const val RESULT_COMMENTS_PAGE_SIZE = 40
        const val RESULT_RESHARE_STATUSES_PAGE_SIZE = 20
    }
}