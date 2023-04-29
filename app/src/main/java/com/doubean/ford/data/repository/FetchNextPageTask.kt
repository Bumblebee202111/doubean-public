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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.doubean.ford.api.ApiResponse
import com.doubean.ford.api.DoubanService
import com.doubean.ford.api.ListResponse
import com.doubean.ford.data.db.AppDatabase
import com.doubean.ford.data.vo.Item
import com.doubean.ford.data.vo.ListResult
import com.doubean.ford.data.vo.Resource
import retrofit2.Call
import java.io.IOException

/**
 * A task that reads the result in the database and fetches the next page, if it has one.
 */
abstract class FetchNextPageTask<ItemType : Item, ResultType : ListResult?, RequestType : ListResponse<ItemType>> internal constructor(//private final String query;
    private val doubanService: DoubanService, private val db: AppDatabase
) : Runnable {
    private val liveData = MutableLiveData<Resource<Boolean>?>()
    override fun run() {
        val current: ResultType? = loadFromDb()
        if (current == null) {
            liveData.postValue(null)
            return
        }
        val nextPageStart = current.next
        if (nextPageStart == null) {
            liveData.postValue(Resource.success(false))
            return
        }
        try {
            val response = createCall(nextPageStart).execute()
            val apiResponse = ApiResponse(response)
            if (apiResponse.isSuccessful) {
                // we merge all repo ids into 1 list so that it is easier to fetch the result list.
                val ids: MutableList<String> = ArrayList()
                ids.addAll(current.ids)
                ids.addAll(apiResponse.body!!.ids)
                val merged =
                    merge(ids, current, apiResponse.body.total, apiResponse.body.nextPageStart)
                db.runInTransaction { saveMergedResult(merged, apiResponse.body.items) }
                liveData.postValue(
                    Resource.success(
                        apiResponse.body.nextPageStart != null
                    )
                )
            } else {
                liveData.postValue(Resource.error(apiResponse.errorMessage!!, true))
            }
        } catch (e: IOException) {
            liveData.postValue(Resource.error(e.message!!, true))
        }
    }

    fun getLiveData(): LiveData<Resource<Boolean>?> {
        return liveData
    }

    protected abstract fun loadFromDb(): ResultType?
    protected abstract fun createCall(nextPageStart: Int?): Call<RequestType>
    protected abstract fun merge(
        ids: List<String>,
        current: ResultType,//TODO: remove
        total: Int,
        nextPageStart: Int?
    ): ResultType

    protected abstract fun saveMergedResult(item: ResultType, items: List<ItemType>)
}