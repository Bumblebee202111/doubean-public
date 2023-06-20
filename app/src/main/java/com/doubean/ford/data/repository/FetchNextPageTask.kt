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

import com.doubean.ford.api.*
import com.doubean.ford.api.model.NetworkPagedList
import com.doubean.ford.data.db.AppDatabase
import com.doubean.ford.data.db.model.PagedListResult
import com.doubean.ford.model.Resource
import java.io.IOException

/**
 * A task that reads the result in the database and fetches the next page, if it has one.
 */
abstract class FetchNextPageTask<ResultEntityType : PagedListResult, RequestType : NetworkPagedList<out Any>> internal constructor(
    private val doubanService: DoubanService, private val db: AppDatabase,
) {

    suspend fun run(): Resource<Boolean>? {
        val current = getCurrentFromDb() ?: return null

        val nextPageStart = current.next ?: return Resource.success(false)

        val newValue = try {
            when (val apiResponse = createCall(nextPageStart)) {
                is ApiSuccessResponse -> {
                    // we merge all item ids into 1 list so that it is easier to fetch the result list.
                    mergeAndSaveCallResult(current, apiResponse.body)
                    Resource.success(apiResponse.body.nextPageStart != null)
                }
                is ApiEmptyResponse -> {
                    Resource.success(false)
                }
                is ApiErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }
            }
        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }
        return newValue
    }

    protected abstract suspend fun getCurrentFromDb(): ResultEntityType?

    protected abstract suspend fun createCall(nextPageStart: Int?): ApiResponse<RequestType>

    protected abstract suspend fun mergeAndSaveCallResult(
        current: ResultEntityType,
        item: RequestType,
    )
}
