package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.model.asExternalModel
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.asEntity
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
                service.getUser(userId)
            },
            mapSuccess = {
                userDao.insertUser(it.asEntity())
            }
        )
    }

    fun getCachedUser(userId: String) = userDao.observeUser(userId).map { it?.asExternalModel() }
}