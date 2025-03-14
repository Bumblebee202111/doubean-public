@file:Suppress("DEPRECATION")

package com.github.bumblebee202111.doubean.feature.groups.topic

import android.graphics.Bitmap
import android.util.Base64
import android.webkit.WebView
import com.google.accompanist.web.AccompanistWebViewClient

open class TopicWebViewClient @JvmOverloads constructor(private val mCssFileNames: List<String> = emptyList()) :
    AccompanistWebViewClient() {

    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        injectCSS(view)
        super.onPageStarted(view, url, favicon)
    }


    
    private fun injectCSS(view: WebView) {
        val js = buildString {
            append("javascript:(function() {")

            mCssFileNames.forEachIndexed { index, cssFileName ->
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