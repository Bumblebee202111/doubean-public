package com.github.bumblebee202111.doubean.model.subjects

import androidx.annotation.Keep

@Keep
enum class SubjectsSearchType(val apiValue: String) {
    MOVIES_AND_TVS("movie"), BOOKS("book");
}

fun SubjectsSearchType.toApiSubjectsSearchType(): String = this.apiValue

fun String.toApiSubjectsSearchType(): SubjectsSearchType? =
    SubjectsSearchType.entries.find { it.apiValue == this }