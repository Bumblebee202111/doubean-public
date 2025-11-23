package com.github.bumblebee202111.doubean.feature.groups.topic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.webkit.URLUtil
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.model.groups.TopicDetail
import com.github.bumblebee202111.doubean.ui.component.DoubeanWebView
import com.github.bumblebee202111.doubean.ui.component.DoubeanWebViewClient
import com.github.bumblebee202111.doubean.util.OpenInUtils
import com.github.bumblebee202111.doubean.util.TOPIC_CSS_FILENAME
import com.google.accompanist.web.rememberWebViewStateWithHTMLData

@Suppress("DEPRECATION")
@SuppressLint("ClickableViewAccessibility")
@Composable
fun ContentWebView(
    topic: TopicDetail,
    html: String,
    onImageClick: (String) -> Unit,
    displayInvalidImageUrl: () -> Unit,
    onOpenDeepLinkUrl: (String, Boolean) -> Boolean,
) {
    val context = LocalContext.current
    val webViewState = rememberWebViewStateWithHTMLData(html)
    
    DoubeanWebView(
        state = webViewState,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .alpha(0.99F),
        captureBackPresses = false,
        onCreated = {
            it.apply {
                setPadding(0, 0, 0, 0)
                setBackgroundColor(Color.TRANSPARENT)
                settings.apply {
                    setNeedInitialFocus(false)
                    
                    useWideViewPort = true
                }

                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP
                    ) {
                        val webViewHitTestResult = getHitTestResult()
                        if (webViewHitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
                            webViewHitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
                        ) {
                            val imageUrl = webViewHitTestResult.extra
                            if (imageUrl != null && URLUtil.isValidUrl(imageUrl)) {
                                val largeImageUrl = topic.images
                                    ?.firstOrNull { it.normal.url == imageUrl }
                                    ?.large?.url
                                    ?: imageUrl
                                onImageClick(largeImageUrl)
                                return@setOnTouchListener true
                            } else if (imageUrl != null) {
                                Log.w("ContentWebView", "Invalid image URL clicked: $imageUrl")
                                displayInvalidImageUrl()
                            }
                        }
                    }
                    return@setOnTouchListener false
                }
            }
        },
        client = remember {
            object : DoubeanWebViewClient(listOf(TOPIC_CSS_FILENAME)) {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    url: String?,
                ): Boolean {
                    return url?.let { handleUrlLoading(context, it, onOpenDeepLinkUrl) } == true
                }
            }
        },
    )
}

private const val CONTENT_WEB_VIEW_TAG = "ContentWebView"

private fun handleUrlLoading(
    context: Context,
    url: String,
    onOpenDeepLinkUrl: (String, Boolean) -> Boolean,
): Boolean {
    Log.d(CONTENT_WEB_VIEW_TAG, "Handling URL: $url")

    if (onOpenDeepLinkUrl(url, false)) {
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