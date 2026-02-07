package com.github.bumblebee202111.doubean.feature.imageviewer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.data.repository.ImageRepository
import com.github.bumblebee202111.doubean.feature.imageviewer.navigation.ImageViewerRoute
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.toUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    savedStateHandle: SavedStateHandle,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {
    val imageUrl = savedStateHandle.toRoute<ImageViewerRoute>().imageUrl

    fun saveImage() {
        viewModelScope.launch {
            imageRepository.saveImage(imageUrl)
                .onSuccess {
                    snackbarManager.showMessage(
                        R.string.image_saved_to.toUiMessage(it.absolutePath)
                    )
                }
                .onFailure {
                    snackbarManager.showMessage(
                        R.string.image_not_found_in_cache.toUiMessage()
                    )
                }
        }
    }
}