package com.github.bumblebee202111.doubean.feature.image

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
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

    val writeExternalStoragePermissionState = rememberPermissionState(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val interactionSource = remember { MutableInteractionSource() }


    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
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

                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q || writeExternalStoragePermissionState.status.isGranted) {
                                context.imageLoader.diskCache
                                    ?.openSnapshot(imageUrl)
                                    ?.use { snapshot ->
                                        val cachedFile = snapshot.data.toFile()
                                        val filename = imageUrl.toUri().lastPathSegment!!
                                        val pathName =
                                            "${Environment.getExternalStorageDirectory().absolutePath}/${Environment.DIRECTORY_PICTURES}/Douban"
                                        val path = File(pathName)
                                        if (!path.exists()) path.mkdir()
                                        val fullPath = File(path, filename)
                                        try {
                                            Files.copy(
                                                cachedFile.toPath(),
                                                fullPath.toPath(),
                                                StandardCopyOption.REPLACE_EXISTING
                                            )

                                            onShowMessage("Image saved to ${fullPath.absolutePath}")

                                        } catch (e: IOException) {
                                            onShowMessage("Error $e")

                                            e.printStackTrace()
                                        }
                                    } ?: onShowMessage("Unknown error")
                            } else {
                                writeExternalStoragePermissionState.launchPermissionRequest()

                            }
                        }
                    ),
            )
        }
    }
}