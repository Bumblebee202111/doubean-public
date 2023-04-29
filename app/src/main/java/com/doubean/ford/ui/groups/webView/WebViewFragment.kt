package com.doubean.ford.ui.groups.webView

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.doubean.ford.databinding.FragmentWebViewBinding
import com.doubean.ford.ui.common.DoubeanWebView
import com.doubean.ford.ui.common.DoubeanWebViewClient
import com.doubean.ford.util.Constants

class WebViewFragment : Fragment() {
    lateinit var binding: FragmentWebViewBinding
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebViewBinding.inflate(inflater, container, false)
        val args = WebViewFragmentArgs.fromBundle(requireArguments())
        val url = args.url
        val webView: DoubeanWebView = binding.webView
        val webViewClient: DoubeanWebViewClient =
            object : DoubeanWebViewClient(Constants.DOUBAN_WEB_CSS_FILENAME) {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    binding.toolbar.title = view.title
                }
            }
        webView.webViewClient = webViewClient
        val webSettings: WebSettings = binding.webView.settings
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 8.1; Nexus 7 Build/JSS15Q) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
        webView.loadUrl(url)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        return binding.root
    }
}