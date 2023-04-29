package com.doubean.ford.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.webkit.WebView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

class DoubeanWebView : WebView {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        if (isInEditMode) {
            return
        }
        initWebView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (isInEditMode) {
            return
        }
        initWebView()
    }

    private fun initWebView() {
        initWebSettings()
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        visibility = GONE
        setPadding(0, 0, 0, 0)
        useDayNightThemes()
    }

    fun useDayNightThemes() {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> WebSettingsCompat.setForceDark(
                    settings, WebSettingsCompat.FORCE_DARK_ON
                )
                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> WebSettingsCompat.setForceDark(
                    settings, WebSettingsCompat.FORCE_DARK_OFF
                )
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initWebSettings() {
        val settings = settings
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
    }
}