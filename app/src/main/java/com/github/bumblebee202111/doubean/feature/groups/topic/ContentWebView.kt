package com.github.bumblebee202111.doubean.feature.groups.topic

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.webkit.URLUtil
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.model.SizedImage
import com.github.bumblebee202111.doubean.model.groups.TopicDetail
import com.github.bumblebee202111.doubean.ui.component.DoubeanWebView
import com.github.bumblebee202111.doubean.ui.component.DoubeanWebViewClient
import com.github.bumblebee202111.doubean.util.AppAndDeviceInfoProvider
import com.github.bumblebee202111.doubean.util.OpenInUtils
import com.github.bumblebee202111.doubean.util.TOPIC_CSS_FILENAME
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay

private const val CONTENT_WEB_VIEW_TAG = "ContentWebView"

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ContentWebViewEntryPoint {
    fun appAndDeviceInfoProvider(): AppAndDeviceInfoProvider
}

@Suppress("DEPRECATION")
@SuppressLint("ClickableViewAccessibility")
@Composable
fun ContentWebView(
    topic: TopicDetail,
    html: String,
    onImageClick: (String) -> Unit,
    displayInvalidImageUrl: () -> Unit,
    onOpenDeepLinkUrl: (String) -> Boolean,
) {
    val context = LocalContext.current
    val appAndDeviceInfoProvider = remember {
        EntryPoints.get(context.applicationContext, ContentWebViewEntryPoint::class.java)
            .appAndDeviceInfoProvider()
    }
    val rexxarUA = remember { appAndDeviceInfoProvider.getRexxarUserAgent() }

    val webViewState = rememberWebViewStateWithHTMLData(html)
    

    val backgroundColor = MaterialTheme.colorScheme.surface.toArgb()

    val density = LocalDensity.current
    val orientation = LocalConfiguration.current.orientation

    var savedHeightPx by rememberSaveable(orientation) { mutableIntStateOf(0) }
    var isRestoring by remember(savedHeightPx) { mutableStateOf(savedHeightPx > 0) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    
    val isLoading = webViewState.isLoading
    LaunchedEffect(isLoading, isRestoring) {
        if (!isLoading && isRestoring) {
            delay(400) 
            isRestoring = false
        }
    }

    
    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.height?.let { height ->
                if (height > 0) savedHeightPx = height
            }
        }
    }

    DoubeanWebView(
        state = webViewState,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .then(
                if (isRestoring && savedHeightPx > 0) {
                    Modifier.heightIn(min = with(density) { savedHeightPx.toDp() })
                } else {
                    Modifier
                }
            ),
        captureBackPresses = false,
        onCreated = { webView ->
            webViewRef = webView

            
            webView.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
                if (isRestoring && v.height >= savedHeightPx - 50) {
                    isRestoring = false
                }
            }

            webView.apply {
                setPadding(0, 0, 0, 0)
                setBackgroundColor(backgroundColor)

                settings.apply {
                    setNeedInitialFocus(false)
                    userAgentString = rexxarUA
                    useWideViewPort = true
                }

                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        return@setOnTouchListener handleWebViewImageClick(
                            hitTestResult = hitTestResult,
                            topicImages = topic.images,
                            onImageClick = onImageClick,
                            displayInvalidImageUrl = displayInvalidImageUrl
                        )
                    }
                    return@setOnTouchListener false
                }
            }
        },
        client = remember {
            object : DoubeanWebViewClient(listOf(TOPIC_CSS_FILENAME)) {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?,
                ): Boolean {
                    val url = request?.url?.toString()
                    return url?.let { handleUrlLoading(context, it, onOpenDeepLinkUrl) } == true
                }
            }
        },
    )
}


private fun handleWebViewImageClick(
    hitTestResult: WebView.HitTestResult,
    topicImages: List<SizedImage>?,
    onImageClick: (String) -> Unit,
    displayInvalidImageUrl: () -> Unit,
): Boolean {
    if (hitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
        hitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
    ) {
        val imageUrl = hitTestResult.extra
        if (imageUrl != null && URLUtil.isValidUrl(imageUrl)) {
            val largeImageUrl = topicImages
                ?.firstOrNull { image -> image.normal.url == imageUrl }
                ?.large?.url ?: imageUrl
            onImageClick(largeImageUrl)
            return true
        } else if (imageUrl != null) {
            Log.w(CONTENT_WEB_VIEW_TAG, "Invalid image URL clicked: $imageUrl")
            displayInvalidImageUrl()
        }
    }
    return false
}

private fun handleUrlLoading(
    context: Context,
    url: String,
    onOpenDeepLinkUrl: (String) -> Boolean,
): Boolean {
    Log.d(CONTENT_WEB_VIEW_TAG, "Handling URL: $url")

    if (onOpenDeepLinkUrl(url)) {
        Log.i(CONTENT_WEB_VIEW_TAG, "Internal navigation initiated for: $url")
        return true
    } else {
        Log.i(
            CONTENT_WEB_VIEW_TAG,
            "Internal navigation failed (Snackbar shown), attempting to open externally: $url"
        )
        try {
            OpenInUtils.viewInActivity(context, url)
            return true
        } catch (e: Exception) {
            Log.e(
                CONTENT_WEB_VIEW_TAG,
                "Failed to open externally after internal navigation attempt failed for: $url",
                e
            )
            return false
        }
    }
}
