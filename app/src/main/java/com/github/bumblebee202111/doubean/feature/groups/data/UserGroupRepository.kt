package com.github.bumblebee202111.doubean.feature.groups.data

import androidx.room.withTransaction
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.model.UserJoinedGroupIdEntity
import com.github.bumblebee202111.doubean.data.db.model.toSimpleGroup
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.network.api.GroupApiService
import com.github.bumblebee202111.doubean.network.model.fangorns.toCachedGroupEntity
import com.github.bumblebee202111.doubean.network.model.fangorns.toGroupItem
import com.github.bumblebee202111.doubean.network.util.loadCacheAndRefresh
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserGroupRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val apiService: GroupApiService,
) {
    private val userGroupDao = appDatabase.userGroupDao()
    private val groupDao = appDatabase.groupDao()

    fun getUserJoinedGroups(userId: String) = loadCacheAndRefresh(
        getCache = {
            userGroupDao.getUserJoinedGroups(userId)
        },
        mapCacheToCacheDomain = {
            it.map {
                it.toSimpleGroup()
            }
        },
        fetchRemote = {
            apiService.getUserJoinedGroups(userId)
        },
        saveCache = {
            appDatabase.withTransaction {
                userGroupDao.deleteAllUserJoinedGroupIds()
                groupDao.insertCachedGroups(it.groups.map { it.toCachedGroupEntity() })
                userGroupDao.insertUserJoinedGroupIds(it.groups.mapIndexed { index, group ->
                    UserJoinedGroupIdEntity(
                        userId = userId,
                        groupId = group.id,
                        index = index
                    )
                })
            }
        },
        mapResponseToDomain = {
            it.groups.map { it.toGroupItem() }
        })

    suspend fun subscribeGroup(groupId: String): AppResult<Unit> = makeApiCall(
        apiCall = { apiService.subscribeGroup(groupId) },
        mapSuccess = { }
    )

    suspend fun unsubscribeGroup(groupId: String): AppResult<Unit> = makeApiCall(
        apiCall = { apiService.unsubscribeGroup(groupId) },
        mapSuccess = { }
    )
}