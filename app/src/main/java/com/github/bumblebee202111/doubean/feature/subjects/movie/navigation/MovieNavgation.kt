package com.github.bumblebee202111.doubean.feature.subjects.movie.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.subjects.movie.MovieScreen
import com.github.bumblebee202111.doubean.feature.subjects.movie.MovieViewModel
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class MovieNavKey(val movieId: String) : NavKey

fun EntryProviderScope<NavKey>.movieEntry(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    entry<MovieNavKey> { key ->
        MovieScreen(
            onBackClick = onBackClick,
            onLoginClick = onLoginClick,
            onImageClick = onImageClick,
            onUserClick = onUserClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onBookClick = onBookClick,
            viewModel = hiltViewModel<MovieViewModel, MovieViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.movieId)
                }
            )
        )
    }
}

fun Navigator.navigateToMovie(movieId: String) = navigate(MovieNavKey(movieId))
