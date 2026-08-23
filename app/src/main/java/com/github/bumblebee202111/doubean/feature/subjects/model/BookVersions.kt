package com.github.bumblebee202111.doubean.feature.subjects.model

import com.github.bumblebee202111.doubean.model.subjects.Book

data class BookVersions(
    val total: Int,
    val versions: List<Book>,
)