package com.github.bumblebee202111.doubean.feature.mydoulists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.github.bumblebee202111.doubean.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MyDouListsScreen(
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onDouListClick: (String) -> Unit,
) {
    val tabTitles = listOf(
        stringResource(R.string.title_my_collection_dou_list_new),
        stringResource(R.string.title_my_following_dou_list_new)
    )
    val pagerState = rememberPagerState { tabTitles.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.title_slide_menu_collect)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
        ) {
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                    )
                }
            }

            val contentPaddingForTabs =
                PaddingValues(bottom = innerPadding.calculateBottomPadding())

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (page) {
                        0 -> MyCollectedItemsScreen(
                            onTopicClick = onTopicClick,
                            onBookClick = onBookClick,
                            onMovieClick = onMovieClick,
                            onTvClick = onTvClick,
                            onUserClick = onUserClick,
                            onImageClick = onImageClick,
                            onDouListClick = onDouListClick,
                            contentPadding = contentPaddingForTabs
                        )

                        1 -> UserDouListsScreen(
                            onDouListClick = onDouListClick,
                            contentPadding = contentPaddingForTabs
                        )
                    }
                }
            }
        }
    }
}