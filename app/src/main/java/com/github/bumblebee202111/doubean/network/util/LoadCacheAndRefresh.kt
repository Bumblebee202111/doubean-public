package com.github.bumblebee202111.doubean.network.util

import android.util.Log
import com.github.bumblebee202111.doubean.model.CachedAppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException


fun <Response : Any, CacheEntity : Any, CacheDomain : Any, Domain : Any> loadCacheAndRefresh(
    getCache: () -> Flow<CacheEntity?>,
    mapCacheToCacheDomain: (CacheEntity) -> CacheDomain,
    fetchRemote: suspend () -> Response,
    saveCache: suspend (Response) -> Unit,
    mapResponseToDomain: (Response) -> Domain,
): Flow<CachedAppResult<Domain, CacheDomain>> = flow {
    val cacheEntityFlow = getCache()
    val cacheDomainFlow = cacheEntityFlow.map { it?.let(mapCacheToCacheDomain) }

    emit(CachedAppResult.Loading(cacheDomainFlow.firstOrNull()))
    try {
        val response = fetchRemote()
        saveCache(response)

        emit(CachedAppResult.Success(mapResponseToDomain(response)))
    } catch (e: Exception) {
        if (e is CancellationException) throw e

        val appError = handleError(e)
        Log.i(
            "loadCacheAndRefresh",
            "Remote fetch failed. Type: ${appError::class.simpleName}, Original: ${e.message}",
            e
        )

        emitAll(cacheDomainFlow.map { cacheValue ->
            CachedAppResult.Error(error = appError, cache = cacheValue)
        })
    }
}