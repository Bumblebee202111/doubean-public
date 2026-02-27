package com.github.bumblebee202111.doubean.feature.subjects.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.subjects.search.SearchSubjectsScreen
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SearchSubjectsNavKey : NavKey

fun Navigator.navigateToSearchSubjects() {
    navigate(SearchSubjectsNavKey)
}

fun EntryProviderScope<NavKey>.searchSubjectsEntry(
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    entry<SearchSubjectsNavKey> {
        SearchSubjectsScreen(
            onBackClick = onBackClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onBookClick = onBookClick,

            )
    }
}