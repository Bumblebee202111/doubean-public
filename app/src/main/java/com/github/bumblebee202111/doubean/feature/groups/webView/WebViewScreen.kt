@file:Suppress("DEPRECATION")

package com.github.bumblebee202111.doubean.feature.groups.webView

import android.webkit.WebView
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.DoubeanWebView
import com.github.bumblebee202111.doubean.ui.component.DoubeanWebViewClient
import com.github.bumblebee202111.doubean.util.DOUBAN_WEB_CSS_FILENAME
import com.google.accompanist.web.rememberWebViewState


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
                    BackButton(onClick = onBackClick)
                })
        }
    ) { innerPadding ->
        DoubeanWebView(state = rememberWebViewState(url),
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            client = remember {
                object : DoubeanWebViewClient(listOf(DOUBAN_WEB_CSS_FILENAME)) {
                    override fun onPageFinished(view: WebView, url: String?) {
                        super.onPageFinished(view, url)
                        view.isVisible = true
                        webpageTitle = view.title
                    }
                }
            },
            onCreated = {
                it.apply {
                    isVisible = false
                    settings.userAgentString =
                        "Mozilla/5.0 (Linux; Android 8.1; Nexus 7 Build/JSS15Q) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"
                }
            }
        )
    }
}