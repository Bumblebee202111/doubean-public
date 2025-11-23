package com.github.bumblebee202111.doubean.feature.image

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.feature.image.navigation.ImageRoute
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.toUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {
    val imageUrl = savedStateHandle.toRoute<ImageRoute>().imageUrl

    fun showMessage(message: String) {
        snackbarManager.showMessage(message.toUiMessage())
    }

    fun saveImage() {

    }
}