package com.github.bumblebee202111.doubean.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import androidx.room.withTransaction
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.COLUMN_VALUE_GROUP_TAG_ID_ALL
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultGroupItemPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedRecommendedGroup
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItem
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupsResult
import com.github.bumblebee202111.doubean.data.db.model.UserEntity
import com.github.bumblebee202111.doubean.data.db.model.asExternalModel
import com.github.bumblebee202111.doubean.data.paging.GroupSearchResultItemRemoteMediator
import com.github.bumblebee202111.doubean.data.paging.GroupTagTopicRemoteMediator
import com.github.bumblebee202111.doubean.model.GroupDetail
import com.github.bumblebee202111.doubean.model.GroupRecommendationType
import com.github.bumblebee202111.doubean.model.RecommendedGroupItem
import com.github.bumblebee202111.doubean.model.Result
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkRecommendedGroup
import com.github.bumblebee202111.doubean.network.model.NetworkRecommendedGroupItemTopic
import com.github.bumblebee202111.doubean.network.model.asEntity
import com.github.bumblebee202111.doubean.network.model.asPartialEntity
import com.github.bumblebee202111.doubean.network.model.tagCrossRefs
import com.github.bumblebee202111.doubean.network.model.topicRefs
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
    private val ApiService: ApiService,
) {
    private val groupDao = appDatabase.groupDao()
    private val groupTopicDao = appDatabase.groupTopicDao()
    private val userDao = appDatabase.userDao()

    fun getGroup(groupId: String): Flow<Result<GroupDetail>> {
        return offlineFirstApiResultFlow(
            loadFromDb = {
                groupDao.getGroupDetail(groupId).map { it?.asExternalModel() }
            },
            call = {
                ApiService.getGroup(groupId)
            },
            saveSuccess = {
                val groupDetails = asPartialEntity()
                val groupTabs = tabs.map { it.asEntity(groupId) }
                val topicTags = topicTagsNormal?.map { it.asEntity(groupId) }
                appDatabase.withTransaction {
                    groupDao.upsertGroupDetail(groupDetails)
                    groupDao.insertGroupTabs(groupTabs)
                    if (topicTags != null) {
                        groupDao.insertTopicTags(topicTags)
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    fun search(query: String) = Pager(
        PagingConfig(
            pageSize = RESULT_GROUPS_COUNT,
            prefetchDistance = RESULT_GROUPS_COUNT / 2,
            initialLoadSize = RESULT_GROUPS_COUNT
        ),
        remoteMediator = GroupSearchResultItemRemoteMediator(
            query = query, service = ApiService, appDatabase = appDatabase
        ),
        pagingSourceFactory = { groupDao.groupSearchResultPagingSource(query) }
    ).flow.map { it.map(GroupSearchResultGroupItemPartialEntity::asExternalModel) }

    @OptIn(ExperimentalPagingApi::class)
    fun getTopicsPagingData(groupId: String, tagId: String?, sortBy: TopicSortBy) = Pager(
        PagingConfig(
            pageSize = RESULT_TOPICS_PAGE_SIZE,
            prefetchDistance = RESULT_TOPICS_PAGE_SIZE / 2,
            initialLoadSize = RESULT_TOPICS_PAGE_SIZE
        ),
        remoteMediator = GroupTagTopicRemoteMediator(
            groupId = groupId,
            tagId = tagId,
            sortBy = sortBy, service = ApiService, appDatabase = appDatabase
        ),
        pagingSourceFactory = {
            groupDao.groupTagTopicPagingSource(
                groupId = groupId,
                tagId = tagId ?: COLUMN_VALUE_GROUP_TAG_ID_ALL,
                sortBy = sortBy
            )
        }
    ).flow.map { it.map(PopulatedTopicItem::asExternalModel) }

    fun getGroupRecommendation(type: GroupRecommendationType): Flow<Result<List<RecommendedGroupItem>>> {
        return offlineFirstApiResultFlow(
            loadFromDb = {
                groupDao.loadRecommendedGroups(type).flatMapLatest { data ->
                    if (data == null) {
                        flowOf(null)
                    } else {
                        groupDao.loadRecommendedGroups(data.ids).map {
                            it.map(PopulatedRecommendedGroup::asExternalModel)
                        }
                    }
                }
            },
            call = {
                ApiService.getGroupsOfTheDay()
            },
            saveSuccess = {
                val recommendedGroupItemGroups =
                    items.map { it.group.asPartialEntity() }
                val recommendedGroups = items.mapIndexed { index, networkRecommendedGroup ->
                    networkRecommendedGroup.asEntity(
                        type,
                        index + 1
                    )
                }
                val topics = items.flatMap { g ->
                    g.topics.map { p -> p.asPartialEntity(g.group.id) }
                }
                val topicIds =
                    items.flatMap { it.topics.map(NetworkRecommendedGroupItemTopic::id) }
                val topicTagCrossRefs =
                    items.flatMap { it.topics.map(NetworkRecommendedGroupItemTopic::tagCrossRefs) }
                        .flatten()
                val topicsAuthors =
                    items.flatMap { g -> g.topics.map { p -> p.author.asEntity() } }
                        .distinctBy(UserEntity::id)
                val recommendedGroupsResult = RecommendedGroupsResult(
                    type, items.map { it.group.id }
                )
                val recommendedGroupTopicRefs =
                    items.flatMap(NetworkRecommendedGroup::topicRefs)
                appDatabase.withTransaction {
                    groupDao.upsertRecommendedGroupItemGroups(recommendedGroupItemGroups)
                    groupDao.upsertRecommendedGroups(recommendedGroups)
                    groupTopicDao.upsertRecommendedGroupItemTopics(topics)
                    groupTopicDao.deleteTopicTagCrossRefsByTopicIds(topicIds)
                    groupTopicDao.insertTopicTagCrossRefs(topicTagCrossRefs)
                    userDao.insertUsers(topicsAuthors)
                    groupDao.insertRecommendedGroupsResult(recommendedGroupsResult)
                    groupDao.insertRecommendedGroupTopics(recommendedGroupTopicRefs)
                }
            }
        )
    }

    companion object {
        const val RESULT_TOPICS_PAGE_SIZE = 40
    }
}




