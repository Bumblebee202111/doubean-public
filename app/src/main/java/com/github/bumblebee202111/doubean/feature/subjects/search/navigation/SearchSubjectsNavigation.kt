package com.github.bumblebee202111.doubean.feature.subjects.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.subjects.search.SearchSubjectsScreen
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SearchSubjectsNavKey : NavKey

fun Navigator.navigateToSearchSubjects() {
    navigate(SearchSubjectsNavKey)
}

fun EntryProviderScope<NavKey>.searchSubjectsEntry(
    onBackClick: () -> Unit,
    onSubjectClick: (id: String, type: SubjectType) -> Unit,
) {
    entry<SearchSubjectsNavKey> {
        SearchSubjectsScreen(
            onBackClick = onBackClick,
            onSubjectClick = onSubjectClick
        )
    }
}