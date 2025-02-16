package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkUser
import com.github.bumblebee202111.doubean.network.model.asExternalModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val service: ApiService) {
    suspend fun getUser(id: String) = suspendRunCatching {
        service.getUser(id)
    }.map(NetworkUser::asExternalModel)
}