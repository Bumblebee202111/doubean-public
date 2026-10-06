package com.github.bumblebee202111.doubean.data.subject

import com.github.bumblebee202111.doubean.core.network.model.search.NetworkSubjectSearchResults
import com.github.bumblebee202111.doubean.core.network.model.search.NetworkSubjectSubTag
import com.github.bumblebee202111.doubean.core.network.model.search.toSearchResultSubjectItems
import com.github.bumblebee202111.doubean.model.subject.SubjectSearchResult
import com.github.bumblebee202111.doubean.model.subject.SubjectSubTag
import com.github.bumblebee202111.doubean.model.subject.SubjectType
import com.github.bumblebee202111.doubean.model.subject.SubjectsSearchType

fun NetworkSubjectSubTag.toSubjectSubTag() = type.toApiSubjectsSearchType()?.let {
    SubjectSubTag(
        name = name, total = total, type = it
    )
}

fun NetworkSubjectSearchResults.toSubjectSearchResult(): SubjectSearchResult {
    val banned = banned.takeIf { it.isNotEmpty() }
    val items =
        subjects.items.toSearchResultSubjectItems().filter { it.type != SubjectType.UNSUPPORTED }
    val allSupportedTypes = SubjectsSearchType.entries.map(
        SubjectsSearchType::toApiSubjectsSearchType
    )
        .toSet()
    val currentSupportedTypes =
        types?.filter { it.type in allSupportedTypes }?.mapNotNull { it.toSubjectSubTag() }
    val initialType =
        types?.firstOrNull()?.takeIf { it.type in allSupportedTypes }?.toSubjectSubTag()?.type
    return SubjectSearchResult(
        banned = banned,
        items = items,
        types = currentSupportedTypes,
        initialType = initialType
    )
}

fun SubjectsSearchType.toApiSubjectsSearchType(): String = this.apiValue

fun String.toApiSubjectsSearchType(): SubjectsSearchType? =
    SubjectsSearchType.entries.find { it.apiValue == this }
