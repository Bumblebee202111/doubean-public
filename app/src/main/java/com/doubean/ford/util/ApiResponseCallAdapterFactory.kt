package com.doubean.ford.util

import com.doubean.ford.api.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResponseCallAdapterFactory : Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {//LiveData<ApiResponse<User>> ==> Call<ApiResponse<User>>
        if (getRawType(returnType) != Call::class.java || returnType !is ParameterizedType)
            return null
        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != ApiResponse::class.java) {
            return null
        }
        if (responseType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }
        val bodyType = getParameterUpperBound(0, responseType)
        return ApiResponseCallAdapter<Any>(bodyType)
    }
}