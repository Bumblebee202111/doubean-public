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


fun <NetworkR : Any, DbEntity : Any, Domain : Any> networkBoundResource(
    queryDb: () -> Flow<DbEntity?>,
    fetchRemote: suspend () -> NetworkR,
    saveRemoteResponseToDb: suspend (NetworkR) -> Unit,
    mapDbEntityToDomain: (DbEntity) -> Domain,
): Flow<CachedAppResult<Domain, Domain?>> = flow {
    val initialDbDataSnapshot = queryDb().firstOrNull()
    val initialDomainSnapshot = initialDbDataSnapshot?.let(mapDbEntityToDomain)

    
    emit(CachedAppResult.Loading(initialDomainSnapshot))

    try {
        val remoteResponse = fetchRemote()
        saveRemoteResponseToDb(remoteResponse)

        
        
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
        
        emitAll(queryDb().map { dbEntity ->
            CachedAppResult.Error(error = appError, cache = dbEntity?.let(mapDbEntityToDomain))
        })
    }
}
