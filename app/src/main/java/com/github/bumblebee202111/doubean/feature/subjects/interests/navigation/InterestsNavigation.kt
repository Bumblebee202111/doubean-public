package com.github.bumblebee202111.doubean.feature.subjects.interests.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.subjects.interests.InterestsScreen
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import kotlinx.serialization.Serializable

@Serializable
data class InterestsRoute(
    val userId: String,
    val subjectType: SubjectType,
)

fun NavController.navigateToInterests(userId: String, subjectType: SubjectType) {
    navigate(InterestsRoute(userId, subjectType))
}

fun NavGraphBuilder.interestsScreen(
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    composable<InterestsRoute> {
        InterestsScreen(
            onBackClick = onBackClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onBookClick = onBookClick
        )
    }
}