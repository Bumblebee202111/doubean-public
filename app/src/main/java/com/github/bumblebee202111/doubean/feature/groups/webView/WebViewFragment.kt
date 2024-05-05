package com.github.bumblebee202111.doubean.feature.groups.webView

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.bumblebee202111.doubean.databinding.FragmentWebViewBinding
import com.github.bumblebee202111.doubean.ui.common.DoubanWebViewClient
import com.github.bumblebee202111.doubean.util.DOUBAN_WEB_CSS_FILENAME

class WebViewFragment : Fragment() {
    private lateinit var binding: FragmentWebViewBinding
    private val args: WebViewFragmentArgs by navArgs()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWebViewBinding.inflate(inflater, container, false)

        val url = args.url
        val webView = binding.webView
        val webViewClient =
            object : DoubanWebViewClient(listOf(DOUBAN_WEB_CSS_FILENAME)) {
                override fun onPageFinished(view: WebView, url: String?) {
                    super.onPageFinished(view, url)
                    binding.toolbar.title = view.title
                }
            }
        webView.webViewClient = webViewClient
        val webSettings = binding.webView.settings
        webSettings.userAgentString =
            "Mozilla/5.0 (Linux; Android 8.1; Nexus 7 Build/JSS15Q) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"
        webView.loadUrl(url)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        return binding.root
    }
}