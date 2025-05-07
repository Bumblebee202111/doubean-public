package com.github.bumblebee202111.doubean.network.util

import android.util.Log
import com.github.bumblebee202111.doubean.model.ApiError
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.ConnectionError
import com.github.bumblebee202111.doubean.model.GenericError
import com.github.bumblebee202111.doubean.network.model.NetworkApiError
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.request
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException


internal suspend fun handleError(e: Exception): AppError = when (e) {
    is ClientRequestException -> handleClientError(e)
    is ServerResponseException -> handleServerError(e)
    is IOException -> handleNetworkError(e)
    is CancellationException -> throw e
    else -> handleGenericError(e)
}

private suspend fun handleServerError(e: ServerResponseException): ApiError {
    val status = e.response.status.value
    val requestUrl = e.response.request.url.toString()
    return try {
        val serverError = e.response.body<NetworkApiError>()
        ApiError(
            status = status,
            code = serverError.code,
            message = serverError.nonBlankMessage,
            request = serverError.request,
            originalError = e
        )
    } catch (parseEx: Exception) {
        Log.w(
            "handleServerError",
            "Failed to parse error response body for $status status. URL: $requestUrl",
            parseEx
        )
        ApiError(
            status = status,
            message = "Server error occurred (${status}).",
            code = status,
            request = requestUrl,
            originalError = e
        )
    }
}

private suspend fun handleClientError(e: ClientRequestException): ApiError {
    val status = e.response.status.value
    val requestUrl = e.response.request.url.toString()
    return try {
        val apiError = e.response.body<NetworkApiError>()
        ApiError(
            status = status,
            code = apiError.code,
            message = apiError.nonBlankMessage,
            request = apiError.request,
            solutionUri = apiError.extra?.solutionUri,
            originalError = e
        )
    } catch (parseEx: Exception) {
        Log.w(
            "handleClientError",
            "Failed to parse error response body for $status status. URL: $requestUrl",
            parseEx
        )
        ApiError(
            status = status,
            code = status,
            message = "Client error occurred (${status}).",
            request = requestUrl,
            originalError = e
        )
    }
}

private fun handleNetworkError(e: IOException): ConnectionError {
    return ConnectionError(e)
}

private fun handleGenericError(e: Exception): GenericError {
    return GenericError(e)
}

private val NetworkApiError.nonBlankMessage
    get() = localizedMessage.takeUnless { it.isNullOrBlank() }
        ?: message.takeUnless { it.isBlank() }