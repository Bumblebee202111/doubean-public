package com.github.bumblebee202111.doubean.network.util

import android.util.Log
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.AppResult
import kotlin.coroutines.cancellation.CancellationException


suspend fun <T : Any, R : Any> makeApiCall(
    apiCall: suspend () -> T,
    mapSuccess: suspend (T) -> R,
): AppResult<R> {
    return try {
        val response = apiCall()
        val parsedData = mapSuccess(response)
        AppResult.Success(parsedData)
    } catch (e: Exception) {
        if (e is CancellationException) throw e

        val appError = handleError(e)
        Log.i(
            "makeApiCall",
            "API call failed. Type: ${appError::class.simpleName}, Original: ${e.message}",
            e
        )
        AppResult.Error(appError)
    }
}