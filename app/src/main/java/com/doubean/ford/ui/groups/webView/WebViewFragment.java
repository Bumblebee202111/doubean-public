package com.doubean.ford.ui.groups.webView;

import android.annotation.SuppressLint;
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

    @SuppressLint("SetJavaScriptEnabled")
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

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 8.1; Nexus 7 Build/JSS15Q) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36");
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(false);
        binding.webView.setVisibility(View.GONE);

        WebViewClient myWebViewClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectCSS();
                view.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
            }
        };
        binding.webView.setWebViewClient(myWebViewClient);
        binding.webView.loadUrl(url);
        return binding.getRoot();
    }

    /**
     * https://stackoverflow.com/a/30018910
     */
    private void injectCSS() {
        try {
            InputStream inputStream = requireContext().getAssets().open(Constants.DOUBAN_WEB_CSS_FILENAME);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            binding.webView.evaluateJavascript("javascript:(function() {" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "document.head.appendChild(style)" +
                    "})()", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
