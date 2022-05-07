package com.doubean.ford.ui.common;

import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.InputStream;

public class DoubeanWebViewClient extends WebViewClient {

    private String mCssFileName;

    public DoubeanWebViewClient() {
        this(null);
    }

    public DoubeanWebViewClient(String cssFileName) {
        super();
        mCssFileName = cssFileName;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (mCssFileName != null)
            injectCSS(view);
        view.setVisibility(View.VISIBLE);
        super.onPageFinished(view, url);
    }

    /**
     * https://stackoverflow.com/a/30018910
     */
    private void injectCSS(WebView view) {
        try {
            InputStream inputStream = view.getContext().getAssets().open(mCssFileName);
            byte[] buffer = new byte[inputStream.available()];
            int byteCount = inputStream.read(buffer);
            inputStream.close();
            if (byteCount != -1) {
                String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
                view.evaluateJavascript("javascript:(function() {" +
                        "var style = document.createElement('style');" +
                        "style.type = 'text/css';" +
                        "style.innerHTML = window.atob('" + encoded + "');" +
                        "document.head.appendChild(style)" +
                        "})()", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
