/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.doubean.ford.data.repository

import androidx.lifecycle.*
import com.doubean.ford.api.ApiEmptyResponse
import com.doubean.ford.api.ApiErrorResponse
import com.doubean.ford.api.ApiResponse
import com.doubean.ford.api.ApiSuccessResponse
import com.doubean.ford.model.Resource
import com.doubean.ford.util.LiveDataUtils.first

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType>
 * @param <RequestType>
</RequestType></ResultType> */

//TODO: rewrite with Flow
abstract class NetworkBoundResource<ResultType, RequestType> {

    val result = liveData {
        val dbSource = loadFromDb().distinctUntilChanged()
        val dbData = dbSource.first()
        emit(Resource.loading(dbData))
        if (shouldFetch(dbData)) {
            fetchFromNetwork(this, dbSource)
        } else {
            emitSource(dbSource.map { newData ->
                Resource.success(newData)
            })
        }
    }

    private suspend fun fetchFromNetwork(
        scope: LiveDataScope<Resource<ResultType>>,
        dbSource: LiveData<ResultType>,
    ) {

        when (val apiResponse = createCall()) {
            is ApiSuccessResponse -> {
                saveCallResult(apiResponse.body)
                scope.emitSource(dbSource.map { newData ->
                    Resource.success(newData)
                })
            }
            is ApiEmptyResponse -> {
                scope.emitSource(dbSource.map { newData ->
                    Resource.success(newData)
                })
            }
            is ApiErrorResponse -> {
                onFetchFailed()
                scope.emitSource(dbSource.map { newData ->
                    Resource.error(apiResponse.errorMessage,
                        newData)
                })
            }
        }
    }

    protected open suspend fun onFetchFailed() {}

    fun asLiveData() = result

    protected abstract suspend fun saveCallResult(item: RequestType)

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract fun loadFromDb(): LiveData<ResultType>

    protected abstract suspend fun createCall(): ApiResponse<RequestType>
}



