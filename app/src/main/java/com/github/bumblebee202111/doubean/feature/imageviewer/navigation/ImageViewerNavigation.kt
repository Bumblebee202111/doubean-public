package com.github.bumblebee202111.doubean.feature.imageviewer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.imageviewer.ImageViewerScreen
import kotlinx.serialization.Serializable

@Serializable
data class ImageViewerRoute(
    val imageUrl: String,
)

fun NavController.navigateToImageViewer(imageUrl: String) {
    this.navigate(ImageViewerRoute(imageUrl = imageUrl))
}

fun NavGraphBuilder.imageViewerScreen(
    navigateUp: () -> Unit,
) {
    composable<ImageViewerRoute> {
        ImageViewerScreen(navigateUp = navigateUp)
    }
}