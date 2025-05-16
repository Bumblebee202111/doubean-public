package com.github.bumblebee202111.doubean.feature.doulists.createddoulists.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.github.bumblebee202111.doubean.feature.doulists.createddoulists.CreatedDouListsScreen
import kotlinx.serialization.Serializable

@Serializable
data class CreatedDouListsRoute(
    val userId: String,
)

fun NavController.navigateToCreatedDouLists(
    userId: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(CreatedDouListsRoute(userId = userId), navOptions)
}

fun NavGraphBuilder.createdDouListsScreen(
    onBackClick: () -> Unit,
    onItemClick: (doulistId: String) -> Unit,
) {
    composable<CreatedDouListsRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "douban:
            }
        )
    ) {
        CreatedDouListsScreen(
            onBackClick = onBackClick,
            onItemClick = onItemClick
        )
    }
}