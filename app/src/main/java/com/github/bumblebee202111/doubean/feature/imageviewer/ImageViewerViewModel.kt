package com.github.bumblebee202111.doubean.feature.imageviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.data.repository.ImageRepository
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.toUiMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ImageViewerViewModel.Factory::class)
class ImageViewerViewModel @AssistedInject constructor(
    private val imageRepository: ImageRepository,
    private val snackbarManager: SnackbarManager,
    @Assisted val imageUrl: String,
) : ViewModel() {

    fun saveImage() {
        viewModelScope.launch {
            imageRepository.saveImage(imageUrl)
                .onSuccess { savedPath ->
                    snackbarManager.showMessage(
                        R.string.image_saved_to.toUiMessage(savedPath)
                    )
                }
                .onFailure { error ->
                    val message = if (error is NoSuchElementException) {
                        R.string.image_not_found_in_cache.toUiMessage()
                    } else {
                        R.string.error_saving_image.toUiMessage(error.message ?: "Unknown error")
                    }
                    snackbarManager.showMessage(message)
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(imageUrl: String): ImageViewerViewModel
    }
}