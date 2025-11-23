package com.github.bumblebee202111.doubean.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource.LoadResult
import androidx.paging.RemoteMediator.MediatorResult
import com.github.bumblebee202111.doubean.model.AppErrorException
import com.github.bumblebee202111.doubean.network.util.handleError
import kotlin.coroutines.cancellation.CancellationException


suspend fun <Key : Any, Value : Any> safePagingLoad(
    block: suspend () -> LoadResult<Key, Value>,
): LoadResult<Key, Value> {
    return try {
        block()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        val appError = handleError(e)
        LoadResult.Error(AppErrorException(appError))
    }
}


@OptIn(ExperimentalPagingApi::class)
suspend fun safeMediatorLoad(
    block: suspend () -> MediatorResult,
): MediatorResult {
    return try {
        block()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        val appError = handleError(e)
        MediatorResult.Error(AppErrorException(appError))
    }
}