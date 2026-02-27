package com.github.bumblebee202111.doubean.feature.subjects.ranklist.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.subjects.ranklist.RankListScreen
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class RankListNavKey(
    val collectionId: String,
) : NavKey

fun EntryProviderScope<NavKey>.rankListEntry(
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    entry<RankListNavKey> {
        RankListScreen(
            onBackClick = onBackClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onBookClick = onBookClick
        )
    }
}

fun Navigator.navigateToRankList(collectionId: String) = navigate(RankListNavKey(collectionId))




