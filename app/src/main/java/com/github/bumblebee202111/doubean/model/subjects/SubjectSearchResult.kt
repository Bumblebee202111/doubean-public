package com.github.bumblebee202111.doubean.model.subjects

data class SubjectSearchResult(
    val banned: String?,
    val items: List<SearchResultSubjectItem>,
    val types: List<SubjectSubTag>?,
    val initialType: SubjectsSearchType?,
)
