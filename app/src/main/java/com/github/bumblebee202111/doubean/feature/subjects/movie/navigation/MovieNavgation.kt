package com.github.bumblebee202111.doubean.feature.subjects.movie.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.subjects.movie.MovieScreen
import kotlinx.serialization.Serializable

@Serializable
data class MovieRoute(val movieId: String)

fun NavGraphBuilder.movieScreen(onBackClick: () -> Unit) {
    composable<MovieRoute> {
        MovieScreen(onBackClick = onBackClick)
    }
}

fun NavController.navigateToMovie(movieId: String) = navigate(MovieRoute(movieId))
