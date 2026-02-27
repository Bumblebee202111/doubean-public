package com.github.bumblebee202111.doubean.feature.imageviewer.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.imageviewer.ImageViewerScreen
import com.github.bumblebee202111.doubean.feature.imageviewer.ImageViewerViewModel
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class ImageViewerNavKey(
    val imageUrl: String,
) : NavKey

fun Navigator.navigateToImageViewer(imageUrl: String) {
    this.navigate(ImageViewerNavKey(imageUrl = imageUrl))
}

fun EntryProviderScope<NavKey>.imageViewerEntry(
    navigateUp: () -> Unit,
) {
    entry<ImageViewerNavKey> { key ->
        ImageViewerScreen(
            navigateUp = navigateUp,
            viewModel = hiltViewModel<ImageViewerViewModel, ImageViewerViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.imageUrl)
                }
            )
        )
    }
}