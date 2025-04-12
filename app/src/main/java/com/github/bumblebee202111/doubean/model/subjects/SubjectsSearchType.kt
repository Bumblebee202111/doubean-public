package com.github.bumblebee202111.doubean.model.subjects

import androidx.annotation.Keep

@Keep
enum class SubjectsSearchType {
    MOVIES_AND_TVS, BOOKS
}

fun SubjectsSearchType.toApiSubjectsSearchType(): String {
    return when (this) {
        SubjectsSearchType.MOVIES_AND_TVS -> "movie"
        SubjectsSearchType.BOOKS -> "book"
    }
}

fun String.toApiSubjectsSearchType(): SubjectsSearchType? {
    return when (this) {
        "movie" -> SubjectsSearchType.MOVIES_AND_TVS
        "book" -> SubjectsSearchType.BOOKS
        else -> null
    }
}