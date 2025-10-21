package com.github.bumblebee202111.doubean.feature.groups.resharestatuses.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.groups.resharestatuses.ReshareStatusesScreen
import kotlinx.serialization.Serializable

@Serializable
data class ReshareStatusesRoute(
    val topicId: String,
)

fun NavController.navigateToReshareStatuses(topicId: String) = navigate(
    route = ReshareStatusesRoute(topicId)
)

fun NavGraphBuilder.reshareStatusesScreen(
    onBackClick: () -> Unit,
    onUserClick: (id: String) -> Unit,
) =
    composable<ReshareStatusesRoute> {
        ReshareStatusesScreen(
            onBackClick = onBackClick,
            onUserClick = onUserClick
        )
    }