package com.github.bumblebee202111.doubean.feature.doulists.doulist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.doulists.doulist.DouListScreen
import kotlinx.serialization.Serializable

@Serializable
data class DouListRoute(val douListId: String)


fun NavController.navigateToDouList(douListId: String, navOptions: NavOptions? = null) {
    this.navigate(route = DouListRoute(douListId = douListId), navOptions = navOptions)
}

fun NavGraphBuilder.douListScreen(
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
) {
    composable<DouListRoute> {
        DouListScreen(
            onBackClick = onBackClick,
            onTopicClick = onTopicClick,
            onBookClick = onBookClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onUserClick = onUserClick,
            onImageClick = onImageClick
        )
    }

}