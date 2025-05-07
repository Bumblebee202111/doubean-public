package com.github.bumblebee202111.doubean.network.util

import android.util.Log
import com.github.bumblebee202111.doubean.model.CachedAppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlin.coroutines.cancellation.CancellationException

/**
 * Implements a network-bound resource strategy where the database is the single source of truth.
 * - Emits [CachedAppResult.Loading] with initial data from the database ([queryDb]).
 * - Attempts to fetch fresh data from [fetchRemote].
 * - If successful, saves it via [saveRemoteResponseToDb]. The flow then relies on [queryDb]
 *   to emit the updated data, which is mapped to [CachedAppResult.Success].
 * - If remote fetch fails, emits [CachedAppResult.Error] and continues to observe
 *   database updates ([queryDb]) for the [CachedAppResult.Error.cache] property.
 *
 * @param NetworkR Type of the raw network response.
 * @param DbEntity Type of the entity stored in and retrieved from the database.
 * @param Domain Type of the domain model for UI consumption.
 */
fun <NetworkR : Any, DbEntity : Any, Domain : Any> networkBoundResource(
    queryDb: () -> Flow<DbEntity?>,
    fetchRemote: suspend () -> NetworkR,
    saveRemoteResponseToDb: suspend (NetworkR) -> Unit,
    mapDbEntityToDomain: (DbEntity) -> Domain,
): Flow<CachedAppResult<Domain, Domain?>> = flow {
    val initialDbDataSnapshot = queryDb().firstOrNull()
    val initialDomainSnapshot = initialDbDataSnapshot?.let(mapDbEntityToDomain)

    // Emit loading with the first available database state
    emit(CachedAppResult.Loading(initialDomainSnapshot))

    try {
        val remoteResponse = fetchRemote()
        saveRemoteResponseToDb(remoteResponse)

        // After saving, emit all subsequent DB updates as Success.
        // mapNotNull ensures we only process non-null DB entities for Success.
        emitAll(queryDb().mapNotNull { dbEntity ->
            dbEntity?.let { CachedAppResult.Success(mapDbEntityToDomain(it)) }
        })
    } catch (e: Exception) {
        if (e is CancellationException) throw e

        val appError = handleError(e)
        Log.i(
            "networkBoundResource",
            "Remote fetch/save failed. Type: ${appError::class.simpleName}, Original: ${e.message}",
            e
        )
        // On remote error, emit Error state but continue providing DB updates for cache
        emitAll(queryDb().map { dbEntity ->
            CachedAppResult.Error(error = appError, cache = dbEntity?.let(mapDbEntityToDomain))
        })
    }
}
