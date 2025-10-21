package com.github.bumblebee202111.doubean.feature.image.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.image.ImageScreen
import kotlinx.serialization.Serializable

@Serializable
data class ImageRoute(val imageUrl: String)

fun NavController.navigateToImage(imageUrl: String) = navigate(
    route = ImageRoute(imageUrl)
)

fun NavGraphBuilder.imageScreen(
    navigateUp: () -> Unit,
) = composable<ImageRoute> {
    ImageScreen(
        navigateUp = navigateUp
    )
}
