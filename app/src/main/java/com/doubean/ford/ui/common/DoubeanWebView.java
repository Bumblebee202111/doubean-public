package com.doubean.ford.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

public class DoubeanWebView extends WebView {


    public DoubeanWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        initWebView();
    }

    public DoubeanWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        initWebView();
    }


    private void initWebView() {
        initWebSettings();
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setVisibility(GONE);
        setPadding(0, 0, 0, 0);
        useDayNightThemes();
    }

    void useDayNightThemes() {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    WebSettingsCompat.setForceDark(getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    WebSettingsCompat.setForceDark(getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                    break;
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    void initWebSettings() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
    }


}
