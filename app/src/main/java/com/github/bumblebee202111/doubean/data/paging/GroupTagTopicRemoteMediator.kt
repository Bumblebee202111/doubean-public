package com.github.bumblebee202111.doubean.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.COLUMN_VALUE_GROUP_TAG_ID_ALL
import com.github.bumblebee202111.doubean.data.db.model.GroupTabTopicRemoteKey
import com.github.bumblebee202111.doubean.data.db.model.GroupTagTopicItemEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPostItem
import com.github.bumblebee202111.doubean.data.db.model.UserEntity
import com.github.bumblebee202111.doubean.model.PostSortBy
import com.github.bumblebee202111.doubean.model.getRequestParamString
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkPostItem
import com.github.bumblebee202111.doubean.network.model.asEntity
import com.github.bumblebee202111.doubean.network.model.asPartialEntity
import com.github.bumblebee202111.doubean.network.model.tagCrossRefs

@OptIn(ExperimentalPagingApi::class)
class GroupTagTopicRemoteMediator(
    private val groupId: String,
    private val tagId: String?,
    private val sortBy: PostSortBy,
    private val service: ApiService,
    private val appDatabase: AppDatabase,
) : RemoteMediator<Int, PopulatedPostItem>() {
    private val groupDao = appDatabase.groupDao()
    private val topicDao = appDatabase.groupTopicDao()
    private val userDao = appDatabase.userDao()
    private val remoteKeyDao = appDatabase.groupTopicRemoteKeyDao()
    private val tagIdColumnValue = tagId ?: COLUMN_VALUE_GROUP_TAG_ID_ALL
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PopulatedPostItem>,
    ): MediatorResult {
        val start = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> appDatabase.withTransaction {
                remoteKeyDao.remoteKey(groupId, tagIdColumnValue, sortBy)
            }.nextKey ?: return MediatorResult.Success(
                endOfPaginationReached = true
            )
        }
        return try {
            val response = service.getGroupTopics(
                groupId = groupId,
                topicTagId = tagId,
                sortBy = sortBy.getRequestParamString(),
                start = start,
                count = if (start == 0) state.config.initialLoadSize else state.config.pageSize
            )

            val topics = response.items.filterIsInstance<NetworkPostItem>().run {
                if (sortBy == PostSortBy.NEW || sortBy == PostSortBy.NEW_TOP)
                    sortedByDescending(NetworkPostItem::created)
                else this
            }
            val nextKey = (start + response.count).takeIf { it < response.total }

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.delete(
                        groupId = groupId,
                        tabId = tagIdColumnValue,
                        sortBy = sortBy
                    )
                    groupDao.deleteGroupTagTopicItems(
                        groupId = groupId,
                        tagId = tagIdColumnValue,
                        sortBy = sortBy
                    )
                }

                remoteKeyDao.insert(
                    GroupTabTopicRemoteKey(
                        groupId = groupId,
                        tabId = tagIdColumnValue,
                        sortBy = sortBy, nextKey
                    )
                )

                val topicItems = topics.mapIndexed { index, networkPostItem ->
                    GroupTagTopicItemEntity(
                        start + index,
                        networkPostItem.id,
                        groupId,
                        tagIdColumnValue,
                        sortBy
                    )
                }

                val topicIds = topics.map(NetworkPostItem::id)

                val postEntities = topics.map { it.asPartialEntity(groupId) }

                val postTagCrossRefs =
                    topics.flatMap(NetworkPostItem::tagCrossRefs)
                val authors = topics.map { it.author.asEntity() }.distinctBy(UserEntity::id)

                groupDao.apply {
                    groupDao.insertGroupTopicItems(topicItems)
                    topicDao.deletePostTagCrossRefsByPostIds(topicIds)
                    topicDao.insertPostTagCrossRefs(postTagCrossRefs)
                    topicDao.upsertPosts(postEntities)
                    userDao.insertUsers(authors)
                }
            }
            MediatorResult.Success(
                endOfPaginationReached = nextKey == null
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }

    }


}
