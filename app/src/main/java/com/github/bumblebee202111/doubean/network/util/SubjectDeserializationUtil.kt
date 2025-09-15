package com.github.bumblebee202111.doubean.network.util

object SubjectDeserializationUtil {
    private val VALID_SUBJECT_TYPES = setOf(
        "movie", "book", "music", "app", "tv", "game", "drama", "podcast"
    )

    private val VALID_DOULIST_TYPES = setOf(
        "doulist", "chart"
    )

    fun isDoulistType(type: String?): Boolean {
        return type in VALID_DOULIST_TYPES
    }

    fun isSubjectType(type: String?): Boolean {
        return type in VALID_SUBJECT_TYPES
    }
}