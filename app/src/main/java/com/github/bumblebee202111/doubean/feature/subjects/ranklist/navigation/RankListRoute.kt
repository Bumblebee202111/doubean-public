package com.github.bumblebee202111.doubean.feature.subjects.ranklist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.subjects.ranklist.RankListScreen
import kotlinx.serialization.Serializable

@Serializable
data class RankListRoute(
    val collectionId: String,
)

fun NavGraphBuilder.rankListScreen(
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    composable<RankListRoute> {
        RankListScreen(
            onBackClick = onBackClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onBookClick = onBookClick
        )
    }
}

fun NavController.navigateToRankList(collectionId: String) = navigate(RankListRoute(collectionId))




