package com.github.bumblebee202111.doubean.feature.subjects.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.subjects.search.SearchSubjectsScreen
import com.github.bumblebee202111.doubean.model.SubjectsSearchType
import kotlinx.serialization.Serializable

@Serializable
data class SearchSubjectsRoute(
    val type: SubjectsSearchType,
)

fun NavController.navigateToSearchSubjects(type: SubjectsSearchType) {
    navigate(SearchSubjectsRoute(type))
}

fun NavGraphBuilder.searchSubjectsScreen(
    onBackClick: () -> Unit, onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    composable<SearchSubjectsRoute> {
        SearchSubjectsScreen(
            onBackClick = onBackClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onBookClick = onBookClick
        )
    }
}