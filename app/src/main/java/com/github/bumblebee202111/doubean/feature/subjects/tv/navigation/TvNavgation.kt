package com.github.bumblebee202111.doubean.feature.subjects.tv.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.subjects.tv.TvScreen
import com.github.bumblebee202111.doubean.feature.subjects.tv.TvViewModel
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class TvNavKey(val tvId: String) : NavKey


fun EntryProviderScope<NavKey>.tvEntry(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    entry<TvNavKey> { key ->
        TvScreen(
            onBackClick = onBackClick,
            onLoginClick = onLoginClick,
            onImageClick = onImageClick,
            onUserClick = onUserClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onBookClick = onBookClick,
            viewModel = hiltViewModel<TvViewModel, TvViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.tvId)
                }
            )
        )
    }
}

fun Navigator.navigateToTv(tvId: String) = navigate(TvNavKey(tvId))
