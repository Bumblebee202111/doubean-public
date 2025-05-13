package com.github.bumblebee202111.doubean.feature.subjects.tv.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.subjects.tv.TvScreen
import kotlinx.serialization.Serializable

@Serializable
data class TvRoute(val tvId: String)

fun NavGraphBuilder.tvScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
) {
    composable<TvRoute> {
        TvScreen(
            onBackClick = onBackClick,
            onLoginClick = onLoginClick,
            onImageClick = onImageClick,
            onUserClick = onUserClick
        )
    }
}

fun NavController.navigateToTv(tvId: String) = navigate(TvRoute(tvId))
