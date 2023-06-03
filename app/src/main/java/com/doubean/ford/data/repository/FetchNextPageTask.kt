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

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.doubean.ford.api.*
import com.doubean.ford.api.model.NetworkPagedList
import com.doubean.ford.data.db.AppDatabase
import com.doubean.ford.data.db.model.PagedListResult
import com.doubean.ford.model.Resource
import retrofit2.Call
import java.io.IOException

/**
 * A task that reads the result in the database and fetches the next page, if it has one.
 */
abstract class FetchNextPageTask<ResultEntityType : PagedListResult, RequestType : NetworkPagedList<out Any>> internal constructor(
//private final String query;
    private val doubanService: DoubanService, private val db: AppDatabase,
) : Runnable {
    private val _liveData = MutableLiveData<Resource<Boolean>?>()
    val liveData: MutableLiveData<Resource<Boolean>?> = _liveData
    override fun run() {
        val current = loadCurrentFromDb()
        if (current == null) {
            _liveData.postValue(null)
            return
        }
        val nextPageStart = current.next
        if (nextPageStart == null) {
            _liveData.postValue(Resource.success(false))
            return
        }
        val newValue = try {
            val response = createCall(nextPageStart).execute()
            when (val apiResponse = ApiResponse.create(response)) {
                is ApiSuccessResponse -> {
                    // we merge all item ids into 1 list so that it is easier to fetch the result list.
                    //val ids = arrayListOf<String>()
                    //ids.addAll(current.ids)
                    //ids.addAll(apiResponseBody.items.map { it.id })
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
        _liveData.postValue(newValue)
    }

    @MainThread
    protected abstract fun loadCurrentFromDb(): ResultEntityType?

    @MainThread
    protected abstract fun createCall(nextPageStart: Int?): Call<RequestType>

    @WorkerThread
    protected abstract fun mergeAndSaveCallResult(current: ResultEntityType, item: RequestType)
}
