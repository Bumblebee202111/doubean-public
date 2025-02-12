package com.github.bumblebee202111.doubean.feature.groups.webView

import android.webkit.WebView
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import com.github.bumblebee202111.doubean.ui.common.DoubanWebViewClient
import com.github.bumblebee202111.doubean.ui.common.DoubeanWebView
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.util.DOUBAN_WEB_CSS_FILENAME


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(url: String, onBackClick: () -> Unit) {

    var webpageTitle by remember {
        mutableStateOf<String?>(null)
    }
    Scaffold(
        topBar = {
            DoubeanTopAppBar(
                titleText =
                webpageTitle,
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                })
        }
    ) { innerPadding ->
        AndroidView(
            factory = {
                DoubeanWebView(it).apply {
                    isVisible = false
                }
            },
            modifier = Modifier.padding(
                start = 16.dp,
                top = innerPadding.calculateTopPadding(),
                end = 16.dp,
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            it.run {
                webViewClient =
                    object : DoubanWebViewClient(listOf(DOUBAN_WEB_CSS_FILENAME)) {
                        override fun onPageFinished(view: WebView, url: String?) {
                            super.onPageFinished(view, url)
                            webpageTitle = view.title
                        }
                    }
                settings.userAgentString =
                    "Mozilla/5.0 (Linux; Android 8.1; Nexus 7 Build/JSS15Q) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"
                loadUrl(url)
            }
        }
    }
}