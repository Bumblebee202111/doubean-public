package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.model.toUser
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.fangorns.UserDetail
import com.github.bumblebee202111.doubean.model.profile.ProfileCommunityContribution
import com.github.bumblebee202111.doubean.network.api.UserApiService
import com.github.bumblebee202111.doubean.network.model.fangorns.toUserDetail
import com.github.bumblebee202111.doubean.network.model.fangorns.toUserEntity
import com.github.bumblebee202111.doubean.network.model.profile.toProfileCommunityContribution
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: UserApiService,
    private val appDatabase: AppDatabase,
) {
    private val userDao = appDatabase.userDao()
    suspend fun fetchUser(userId: String): AppResult<Unit> {
        return makeApiCall(
            apiCall = {
                apiService.getUserBasicOnly(userId)
            },
            mapSuccess = {
                userDao.insertUser(it.toUserEntity())
            }
        )
    }

    fun getCachedUser(userId: String) = userDao.observeUser(userId).map { it?.toUser() }

    suspend fun getUserDetail(userId: String): AppResult<UserDetail> {
        return makeApiCall(
            apiCall = {
                apiService.getUser(userId)
            },
            mapSuccess = {
                it.toUserDetail()
            }
        )
    }

    suspend fun getUserCommunityContributions(userId: String): AppResult<ProfileCommunityContribution> {
        return makeApiCall(
            apiCall = {
                apiService.getUserCommunityContribution(userId)
            },
            mapSuccess = {
                it.toProfileCommunityContribution()
            }
        )
    }
}