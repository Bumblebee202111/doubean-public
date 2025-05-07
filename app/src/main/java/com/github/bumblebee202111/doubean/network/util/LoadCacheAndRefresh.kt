package com.github.bumblebee202111.doubean.network.util

import android.util.Log
import com.github.bumblebee202111.doubean.model.CachedAppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

/**
 * Implements a "cache then network" strategy.
 * - Emits [CachedAppResult.Loading] with data from [getCache].
 * - Fetches new data using [fetchRemote].
 * - If successful, saves it via [saveCache] and emits [CachedAppResult.Success] with the mapped remote response.
 * - If remote fetch fails, emits [CachedAppResult.Error] and continues to emit updates from [getCache]
 *   as the `cache` property of the error state.
 *
 * @param Response Type of the raw network response.
 * @param CacheEntity Type of the entity stored in cache.
 * @param CacheDomain Type of the domain model mapped from the cache.
 * @param Domain Type of the domain model mapped from the network response.
 */
fun <Response : Any, CacheEntity : Any, CacheDomain : Any, Domain : Any> loadCacheAndRefresh(
    getCache: () -> Flow<CacheEntity?>,
    mapCacheToCacheDomain: (CacheEntity) -> CacheDomain,
    fetchRemote: suspend () -> Response,
    saveCache: suspend (Response) -> Unit,
    mapResponseToDomain: (Response) -> Domain,
): Flow<CachedAppResult<Domain, CacheDomain>> = flow {
    val cacheEntityFlow = getCache()
    val cacheDomainFlow = cacheEntityFlow.map { it?.let(mapCacheToCacheDomain) }
    // Emit loading with the first available cache state
    emit(CachedAppResult.Loading(cacheDomainFlow.firstOrNull()))
    try {
        val response = fetchRemote()
        saveCache(response)
        // Emit success with the freshly fetched and mapped network data
        emit(CachedAppResult.Success(mapResponseToDomain(response)))
    } catch (e: Exception) {
        if (e is CancellationException) throw e

        val appError = handleError(e)
        Log.i(
            "loadCacheAndRefresh",
            "Remote fetch failed. Type: ${appError::class.simpleName}, Original: ${e.message}",
            e
        )
        // On remote error, emit Error state but continue providing cache updates
        emitAll(cacheDomainFlow.map { cacheValue ->
            CachedAppResult.Error(error = appError, cache = cacheValue)
        })
    }
}