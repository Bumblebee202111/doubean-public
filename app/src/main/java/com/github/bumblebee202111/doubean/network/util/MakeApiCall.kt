package com.github.bumblebee202111.doubean.network.util

import android.util.Log
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.AppResult
import kotlin.coroutines.cancellation.CancellationException

/**
 * Makes a network API call and maps the response or handles errors.
 *
 * @param T The type of the raw API response.
 * @param R The type of the successfully mapped data.
 * @param apiCall Suspended function to perform the actual network request.
 * @param mapSuccess Suspended function to transform the raw response [T] to the desired type [R].
 * @return [AppResult.Success] with mapped data or [AppResult.Error] with an [AppError].
 */
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