package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.model.asExternalModel
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.fangorns.UserDetail
import com.github.bumblebee202111.doubean.model.profile.ProfileCommunityContribution
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.fangorns.asEntity
import com.github.bumblebee202111.doubean.network.model.fangorns.toUserDetail
import com.github.bumblebee202111.doubean.network.model.profile.toProfileCommunityContribution
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val service: ApiService,
    private val appDatabase: AppDatabase,
) {
    private val userDao = appDatabase.userDao()
    suspend fun fetchUser(userId: String): AppResult<Unit> {
        return makeApiCall(
            apiCall = {
                service.getUserBasicOnly(userId)
            },
            mapSuccess = {
                userDao.insertUser(it.asEntity())
            }
        )
    }

    fun getCachedUser(userId: String) = userDao.observeUser(userId).map { it?.asExternalModel() }

    suspend fun getUserDetail(userId: String): AppResult<UserDetail> {
        return makeApiCall(
            apiCall = {
                service.getUser(userId)
            },
            mapSuccess = {
                it.toUserDetail()
            }
        )
    }

    suspend fun getUserCommunityContributions(userId: String): AppResult<ProfileCommunityContribution> {
        return makeApiCall(
            apiCall = {
                service.getUserCommunityContribution(userId)
            },
            mapSuccess = {
                it.toProfileCommunityContribution()
            }
        )
    }
}