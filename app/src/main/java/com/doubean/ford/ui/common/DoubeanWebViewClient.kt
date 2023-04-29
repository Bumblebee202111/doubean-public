package com.doubean.ford.ui.common

import android.util.Base64
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient

open class DoubeanWebViewClient @JvmOverloads constructor(private val mCssFileName: String? = null) :
    WebViewClient() {
    override fun onPageFinished(view: WebView, url: String) {
        if (mCssFileName != null) injectCSS(view)
        view.visibility = View.VISIBLE
        super.onPageFinished(view, url)
    }

    /**
     * https://stackoverflow.com/a/30018910
     */
    private fun injectCSS(view: WebView) {
        try {
            val inputStream = view.context.assets.open(mCssFileName!!)
            val buffer = ByteArray(inputStream.available())
            val byteCount = inputStream.read(buffer)
            inputStream.close()
            if (byteCount != -1) {
                val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
                view.evaluateJavascript(
                    "javascript:(function() {" +
                            "var style = document.createElement('style');" +
                            "style.type = 'text/css';" +
                            "style.innerHTML = window.atob('" + encoded + "');" +
                            "document.head.appendChild(style)" +
                            "})()", null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}