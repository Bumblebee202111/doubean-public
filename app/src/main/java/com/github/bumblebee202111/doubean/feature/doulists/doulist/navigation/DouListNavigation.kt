package com.github.bumblebee202111.doubean.feature.doulists.doulist.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.doulists.doulist.DouListScreen
import com.github.bumblebee202111.doubean.feature.doulists.doulist.DouListViewModel
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class DouListNavKey(val douListId: String) : NavKey


fun Navigator.navigateToDouList(douListId: String) {
    this.navigate(key = DouListNavKey(douListId = douListId))
}

fun EntryProviderScope<NavKey>.douListEntry(
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
) {
    entry<DouListNavKey> {
        DouListScreen(
            onBackClick = onBackClick,
            onTopicClick = onTopicClick,
            onBookClick = onBookClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onUserClick = onUserClick,
            onImageClick = onImageClick,
            viewModel = hiltViewModel<DouListViewModel, DouListViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(it.douListId)
                }
            )
        )
    }

}