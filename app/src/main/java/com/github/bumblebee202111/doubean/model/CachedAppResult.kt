package com.github.bumblebee202111.doubean.model

sealed class CachedAppResult<out T, out C> {
    data class Loading<out C>(val cache: C? = null) : CachedAppResult<Nothing, C>()
    data class Success<out T>(val data: T) : CachedAppResult<T, Nothing>()
    data class Error<out C>(val error: AppError, val cache: C? = null) :
        CachedAppResult<Nothing, C>()
}

val <T>CachedAppResult<T, T>.data
    get() = when (this) {
        is CachedAppResult.Error -> cache
        is CachedAppResult.Loading -> cache
        is CachedAppResult.Success -> data
    }