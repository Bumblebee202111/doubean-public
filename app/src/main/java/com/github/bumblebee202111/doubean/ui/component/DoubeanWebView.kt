@file:Suppress("DEPRECATION")

package com.github.bumblebee202111.doubean.ui.component

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Base64
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewNavigator
import org.jetbrains.annotations.MustBeInvokedByOverriders

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DoubeanWebView(
    state: WebViewState,
    modifier: Modifier = Modifier,
    captureBackPresses: Boolean = true,
    navigator: WebViewNavigator = rememberWebViewNavigator(),
    client: DoubeanWebViewClient = remember { DoubeanWebViewClient() },
    onCreated: (WebView) -> Unit = {},
) {
    WebView(
        state = state,
        modifier = modifier,
        captureBackPresses = captureBackPresses,
        navigator = navigator,
        client = client,
        onCreated = { webView ->
            webView.apply {
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                setPadding(0, 0, 0, 0)
                settings.apply {
                    javaScriptEnabled = true
                    builtInZoomControls = true
                    displayZoomControls = false
                    useWideViewPort = true
                    setNeedInitialFocus(false)
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                        WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, true)
                    }
                }

                
                onCreated(this)
            }
        }
    )
}

open class DoubeanWebViewClient @JvmOverloads constructor(private val cssFileNames: List<String> = emptyList()) :
    AccompanistWebViewClient() {

    @MustBeInvokedByOverriders
    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        injectCSS(view)
        super.onPageStarted(view, url, favicon)
    }

    @MustBeInvokedByOverriders
    override fun onPageFinished(view: WebView, url: String?) {
        injectCSS(view)
        super.onPageFinished(view, url)
    }

    
    private fun injectCSS(view: WebView) {
        val js = buildString {
            append("javascript:(function() {")
            cssFileNames.forEachIndexed { index, cssFileName ->
                try {
                    val inputStream = view.context.assets.open(cssFileName)
                    val buffer = ByteArray(inputStream.available())
                    val byteCount = inputStream.read(buffer)
                    inputStream.close()
                    if (byteCount != -1) {
                        val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
                        append("var style$index = document.createElement('style');")
                        append("style$index.type = 'text/css';")
                        append("style$index.innerHTML = window.atob('$encoded');")
                        append("document.head.appendChild(style$index);")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            append("})()")
        }
        view.evaluateJavascript(js, null)
    }
}