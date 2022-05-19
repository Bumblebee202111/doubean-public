package com.doubean.ford.ui.groups.webView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.doubean.ford.databinding.FragmentWebViewBinding;
import com.doubean.ford.ui.common.DoubeanWebView;
import com.doubean.ford.ui.common.DoubeanWebViewClient;
import com.doubean.ford.util.Constants;

public class WebViewFragment extends Fragment {
    FragmentWebViewBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentWebViewBinding.inflate(inflater, container, false);
        WebViewFragmentArgs args = WebViewFragmentArgs.fromBundle(getArguments());
        String url = args.getUrl();

        DoubeanWebView webView = binding.webView;

        DoubeanWebViewClient webViewClient = new DoubeanWebViewClient(Constants.DOUBAN_WEB_CSS_FILENAME) {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.toolbar.setTitle(view.getTitle());
            }
        };

        webView.setWebViewClient(webViewClient);

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 8.1; Nexus 7 Build/JSS15Q) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36");

        webView.loadUrl(url);

        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        return binding.getRoot();
    }


}
