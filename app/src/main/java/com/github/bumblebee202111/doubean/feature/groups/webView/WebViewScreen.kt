@file:Suppress("DEPRECATION")

package com.github.bumblebee202111.doubean.feature.groups.webView

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.DoubeanWebView
import com.github.bumblebee202111.doubean.ui.component.DoubeanWebViewClient
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator
import com.github.bumblebee202111.doubean.util.DOUBAN_WEB_CSS_FILENAME
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(url: String, onBackClick: () -> Unit) {
    val webViewState = rememberWebViewState(url)
    val navigator = rememberWebViewNavigator()

    val loadingState = webViewState.loadingState
    val errors = webViewState.errorsForCurrentRequest

    val loadingTitle = stringResource(R.string.loading)

    val title = when (loadingState) {
        is LoadingState.Initializing -> loadingTitle
        is LoadingState.Loading -> webViewState.pageTitle ?: loadingTitle
        is LoadingState.Finished -> webViewState.pageTitle
    }

    Scaffold(
        topBar = {
            DoubeanTopAppBar(
                titleText = title,
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                })
        }
    ) { innerPadding ->
        when {
            errors.isNotEmpty() -> {
                FullScreenErrorWithRetry(
                    message = errors.first().error.description?.toString()
                        ?: stringResource(R.string.error_loading_failed),
                    onRetryClick = { navigator.reload() },
                    contentPadding = innerPadding
                )
            }

            loadingState !is LoadingState.Finished -> {
                FullScreenLoadingIndicator(contentPadding = innerPadding)
            }

            else -> {
                DoubeanWebView(
                    state = webViewState,
                    navigator = navigator,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    client = remember {
                        DoubeanWebViewClient(listOf(DOUBAN_WEB_CSS_FILENAME))
                    },
                    onCreated = {
                        it.settings.userAgentString =
                            "Mozilla/5.0 (Linux; Android 8.1; Nexus 7 Build/JSS15Q) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"
                    }
                )
            }
        }
    }
}