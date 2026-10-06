package com.github.bumblebee202111.doubean.model.subject

sealed interface Rating {
    data class NonNull(
        val value: Float, val max: Int,
    ) : Rating

    data class Null(val reason: String) : Rating
}