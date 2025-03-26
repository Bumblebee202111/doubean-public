package com.github.bumblebee202111.doubean.model

sealed interface AppResult<out T : Any> : LoginResult {
    data class Success<T : Any>(val data: T) : AppResult<T>
    data class Error<T : Any>(val error: AppError) : AppResult<T>
}