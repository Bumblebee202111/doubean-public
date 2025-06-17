package com.github.bumblebee202111.doubean.feature.mydoulists.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.mydoulists.MyDouListsScreen
import kotlinx.serialization.Serializable

@Serializable
object MyDouListsRoute

fun NavController.navigateToMyDouLists(navOptions: NavOptions? = null) {
    this.navigate(MyDouListsRoute, navOptions)
}

fun NavGraphBuilder.myDouListsScreen(
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onImageClick: (imageUrl: String) -> Unit,
    onDouListClick: (douListId: String) -> Unit,
) {
    composable<MyDouListsRoute> {
        MyDouListsScreen(
            onBackClick = onBackClick,
            onTopicClick = onTopicClick,
            onBookClick = onBookClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onUserClick = onUserClick,
            onImageClick = onImageClick,
            onDouListClick = onDouListClick
        )
    }
}