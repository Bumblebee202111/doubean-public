package com.github.bumblebee202111.doubean.feature.image

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.imageLoader
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption


@Composable
fun ImageScreen(
    navigateUp: () -> Unit,
    viewModel: ImageViewModel = hiltViewModel(),
) {
    ImageScreen(
        imageUrl = viewModel.imageUrl,
        navigateUp = navigateUp,
        onShowMessage = viewModel::showMessage
    )
}

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class,
    ExperimentalCoilApi::class
)
@Composable
fun ImageScreen(
    imageUrl: String,
    navigateUp: () -> Unit,
    onShowMessage: (String) -> Unit,
) {

    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    val writePermissionState = rememberPermissionState(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    var requestSave by remember { mutableStateOf(false) }


    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    LaunchedEffect(requestSave, writePermissionState.status) {
        if (requestSave && writePermissionState.status.isGranted) {
            saveImage(context, imageUrl, onShowMessage)
            requestSave = false
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable(
                    onClick = navigateUp,
                    interactionSource = interactionSource,
                    indication = null
                ),
            contentAlignment = Alignment.Center
        ) {

            var isLoading by remember { mutableStateOf(true) }
            var isError by remember { mutableStateOf(false) }

            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier


                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        rotationZ = rotation,
                        translationX = offset.x,
                        translationY = offset.y,
                        clip = true
                    )


                    .transformable(state = state)
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = navigateUp,
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)

                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q || writePermissionState.status.isGranted) {
                                saveImage(context, imageUrl, onShowMessage)
                            } else {
                                writePermissionState.launchPermissionRequest()
                                requestSave = true
                            }
                        }
                    ),
                onState = { state ->
                    isLoading = state is AsyncImagePainter.State.Loading
                    isError = state is AsyncImagePainter.State.Error
                },
            )
            if (isLoading) {
                CircularProgressIndicator()
            }

            if (isError) {
                Text(
                    text = "Failed to load image",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun saveImage(
    context: Context,
    imageUrl: String,
    onShowMessage: (String) -> Unit,
) {
    context.imageLoader.diskCache?.openSnapshot(imageUrl)?.use { snapshot ->
        val cachedFile = snapshot.data.toFile()
        val filename = imageUrl.toUri().lastPathSegment
            ?: "douban_image_${System.currentTimeMillis()}.jpg"
        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val doubanDir = File(picturesDir, "Douban")
        val destinationFile = File(doubanDir, filename)
        try {
            Files.copy(
                cachedFile.toPath(),
                destinationFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )

            onShowMessage("Image saved to ${destinationFile.absolutePath}")

        } catch (e: IOException) {
            onShowMessage("Error: ${e.message}")

            e.printStackTrace()
        }
    } ?: onShowMessage("Image not found in cache.")
}