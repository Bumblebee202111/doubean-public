package com.github.bumblebee202111.doubean.feature.imageviewer

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.github.bumblebee202111.doubean.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

private const val MIN_SCALE = 1f
private const val MAX_SCALE = 5f
private const val DOUBLE_TAP_SCALE = 2.5f
private const val ANIMATION_DURATION_MS = 300

@Composable
fun ImageViewerScreen(
    navigateUp: () -> Unit,
    viewModel: ImageViewerViewModel = hiltViewModel(),
) {
    ImageViewerScreen(
        imageUrl = viewModel.imageUrl,
        navigateUp = navigateUp,
        onSave = viewModel::saveImage
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageViewerScreen(
    imageUrl: String,
    navigateUp: () -> Unit,
    onSave: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val writePermissionState = rememberPermissionState(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    var requestSave by remember { mutableStateOf(false) }

    var scale by remember { mutableFloatStateOf(MIN_SCALE) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(MIN_SCALE, MAX_SCALE)
        val maxOffsetX = (size.width * (newScale - 1) / 2f).coerceAtLeast(0f)
        val maxOffsetY = (size.height * (newScale - 1) / 2f).coerceAtLeast(0f)
        val newOffset = (offset + offsetChange).let {
            it.copy(
                x = it.x.coerceIn(-maxOffsetX, maxOffsetX),
                y = it.y.coerceIn(-maxOffsetY, maxOffsetY)
            )
        }
        scale = newScale
        offset = if (scale > MIN_SCALE) newOffset else Offset.Zero
    }

    LaunchedEffect(requestSave, writePermissionState.status) {
        if (requestSave && writePermissionState.status.isGranted) {
            onSave()
            requestSave = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { navigateUp() },
                    onDoubleTap = {
                        scope.launch {
                            if (scale > MIN_SCALE) {
                                launch {
                                    Animatable(scale).animateTo(
                                        MIN_SCALE,
                                        tween(ANIMATION_DURATION_MS)
                                    ) { scale = value }
                                }
                                launch {
                                    Animatable(
                                        offset,
                                        Offset.VectorConverter
                                    ).animateTo(Offset.Zero, tween(ANIMATION_DURATION_MS)) {
                                        offset = value
                                    }
                                }
                            } else {
                                Animatable(scale).animateTo(
                                    DOUBLE_TAP_SCALE,
                                    tween(ANIMATION_DURATION_MS)
                                ) { scale = value }
                            }
                        }
                    },
                    onLongPress = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || writePermissionState.status.isGranted) {
                            onSave()
                        } else {
                            writePermissionState.launchPermissionRequest()
                            requestSave = true
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        var painterState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Loading(null)) }

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
                .transformable(state = transformableState),
            onState = { painterState = it },
        )

        when (painterState) {
            is AsyncImagePainter.State.Loading -> CircularProgressIndicator()
            is AsyncImagePainter.State.Error -> Text(
                text = stringResource(R.string.failed_to_load_image),
                color = MaterialTheme.colorScheme.error
            )

            else -> Unit
        }
    }
}