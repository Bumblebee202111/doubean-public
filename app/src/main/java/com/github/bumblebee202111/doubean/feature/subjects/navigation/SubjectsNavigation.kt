package com.github.bumblebee202111.doubean.feature.subjects.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.subjects.SubjectsScreen
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectsSearchType
import kotlinx.serialization.Serializable

@Serializable
data object SubjectsRoute

fun NavGraphBuilder.subjectsScreen(
    onSettingsClick: () -> Unit,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onSearchClick: (type: SubjectsSearchType) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    composable<SubjectsRoute> {
        SubjectsScreen(
            onSettingsClick = onSettingsClick,
            onSubjectStatusClick = onSubjectStatusClick,
            onLoginClick = onLoginClick,
            onSearchClick = onSearchClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onBookClick = onBookClick
        )
    }
}