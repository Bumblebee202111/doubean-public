package com.doubean.ford.ui.webView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.doubean.ford.databinding.FragmentWebViewBinding;
import com.doubean.ford.util.Constants;

import java.io.InputStream;

public class WebViewFragment extends Fragment {
    FragmentWebViewBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentWebViewBinding.inflate(inflater, container, false);
        WebViewFragmentArgs args = WebViewFragmentArgs.fromBundle(getArguments());
        String url = args.getUrl();
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);

            }
        });
        binding.webView.setVerticalScrollBarEnabled(false);
        binding.webView.setHorizontalScrollBarEnabled(false);
        //binding.webView.setPadding(0, 0, 0, 0);
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36");
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(false);
        binding.webView.setVisibility(View.GONE);
        binding.webView.setWebViewClient(new WebViewClient() {
                                             @Override
                                             public void onPageStarted(WebView view, String url, Bitmap favicon) {

                                                 super.onPageStarted(view, url, favicon);
                                             }

                                             @Override
                                             public void onPageFinished(WebView view, String url) {
                                                 injectCSS();
                                                 binding.webView.setVisibility(View.VISIBLE);
                                                 super.onPageFinished(view, url);

                                             }
                                         }
        );
        binding.webView.loadUrl(url);
        return binding.getRoot();
    }

    /**
     * https://stackoverflow.com/a/30018910
     */
    private void injectCSS() {
        try {
            InputStream inputStream = requireContext().getAssets().open(Constants.WEBVIEW_CSS_FILENAME);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            binding.webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
