package com.github.bumblebee202111.doubean.model

sealed interface AppResult<out R> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Error(val error: AppError) : AppResult<Nothing>
}