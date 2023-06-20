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
package com.doubean.ford.util

import com.doubean.ford.api.ApiResponse
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

/**
 * A Retrofit adapter that converts the suspend Call into a ApiResponse.
 * @param <R>
</R> */
class ApiResponseCallAdapter<R>(private val responseType: Type) :
    CallAdapter<R, Call<ApiResponse<R>>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<R>): Call<ApiResponse<R>> {
        return ApiResponseCall(call)
    }
}

internal class ApiResponseCall<R>(private val delegate: Call<R>) : Call<ApiResponse<R>> {
    override fun clone() = ApiResponseCall(delegate)

    override fun execute(): Response<ApiResponse<R>> {
        val response = delegate.execute()
        return Response.success(response.code(), ApiResponse.create(response))
    }

    override fun enqueue(callback: Callback<ApiResponse<R>>) {
        delegate.enqueue(object : Callback<R> {
            override fun onResponse(call: Call<R>, response: Response<R>) {
                callback.onResponse(this@ApiResponseCall,
                    Response.success(ApiResponse.create(response)))
            }

            override fun onFailure(call: Call<R>, t: Throwable) {
                callback.onResponse(this@ApiResponseCall,
                    Response.success(ApiResponse.create(t)))
            }

        })
    }

    override fun isExecuted() = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled() = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}