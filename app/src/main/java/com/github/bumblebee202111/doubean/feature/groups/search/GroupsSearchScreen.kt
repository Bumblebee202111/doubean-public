package com.github.bumblebee202111.doubean.feature.groups.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.SearchResultGroupItem
import com.github.bumblebee202111.doubean.feature.groups.shared.dayRankingItems
import com.github.bumblebee202111.doubean.feature.search.common.searchHistory
import com.github.bumblebee202111.doubean.model.groups.GroupItemWithIntroInfo
import com.github.bumblebee202111.doubean.model.search.SearchHistory
import com.github.bumblebee202111.doubean.ui.component.DoubeanAppBarWithSearch
import com.github.bumblebee202111.doubean.ui.component.FullScreenCenteredContent
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator
import com.github.bumblebee202111.doubean.ui.component.SectionErrorWithRetry
import com.github.bumblebee202111.doubean.ui.util.toUiMessage

@Composable
fun GroupsSearchScreen(
    onGroupClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: GroupsSearchViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val history by viewModel.searchHistory.collectAsStateWithLifecycle()
    val groupPagingItems = viewModel.results.collectAsLazyPagingItems()
    val dayRankingUiState by viewModel.dayRankingUiState.collectAsStateWithLifecycle()
    GroupsSearchScreen(
        query = query,
        history = history,
        groupPagingItems = groupPagingItems,
        dayRankingUiState = dayRankingUiState,
        onQueryChange = viewModel::onQueryChange,
        onSearchTriggered = viewModel::onSearchTriggered,
        onDeleteHistoryItem = viewModel::onDeleteHistoryItem,
        onClearHistory = viewModel::onClearHistory,
        onGroupClick = onGroupClick,
        onBackClick = onBackClick,
        onRetryDayRanking = viewModel::retryDayRanking,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsSearchScreen(
    query: String,
    history: List<SearchHistory>,
    groupPagingItems: LazyPagingItems<GroupItemWithIntroInfo>,
    dayRankingUiState: DayRankingUiState,
    onQueryChange: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onDeleteHistoryItem: (String) -> Unit,
    onClearHistory: () -> Unit,
    onGroupClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onRetryDayRanking: () -> Unit,
) {
    var isSearchFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val onSearchAndHideKeyboard = { searchInput: String ->
        keyboardController?.hide()
        onSearchTriggered(searchInput)
    }

    Scaffold(
        topBar = {
            DoubeanAppBarWithSearch(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearchAndHideKeyboard,
                onBackClick = onBackClick,
                onFocusChanged = { isSearchFocused = it },
                placeholderText = stringResource(R.string.search_groups_hint)
            )
        }) { innerPadding ->
        when {
            query.isBlank() && isSearchFocused -> {
                LazyColumn(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                    contentPadding = PaddingValues(
                        bottom = innerPadding.calculateBottomPadding() + 12.dp,
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                    )
                ) {
                    searchHistory(
                        history = history,
                        onHistoryClick = onSearchAndHideKeyboard,
                        onDeleteClick = onDeleteHistoryItem,
                        onClearAllClick = onClearHistory
                    )
                    item {
                        Text(
                            text = stringResource(R.string.title_day_ranking),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    when (dayRankingUiState) {
                        is DayRankingUiState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        is DayRankingUiState.Error -> {
                            item {
                                SectionErrorWithRetry(
                                    message = dayRankingUiState.errorMessage.getString(),
                                    onRetryClick = onRetryDayRanking
                                )
                            }
                        }

                        is DayRankingUiState.Success -> {
                            item {
                                Text(
                                    text = stringResource(R.string.title_day_ranking),
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 16.dp,
                                        bottom = 8.dp
                                    ),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            dayRankingItems(dayRankingUiState.items, onGroupClick)
                        }
                    }
                }
            }

            else -> {
                when (val refreshState = groupPagingItems.loadState.refresh) {
                    is LoadState.Loading -> {
                        FullScreenLoadingIndicator(contentPadding = innerPadding)
                    }

                    is LoadState.Error -> {
                        val errorMessage = refreshState.toUiMessage()
                        FullScreenErrorWithRetry(
                            message = errorMessage.getString(),
                            onRetryClick = { groupPagingItems.retry() },
                            contentPadding = innerPadding
                        )
                    }

                    else -> {
                        if (groupPagingItems.itemCount == 0) {
                            FullScreenCenteredContent(contentPadding = innerPadding) {
                                Text(stringResource(R.string.empty_search_result))
                            }
                        } else {
                            GroupList(
                                modifier = Modifier.padding(
                                    top = innerPadding.calculateTopPadding(),
                                    start = 16.dp,
                                    end = 16.dp
                                ),
                                groupPagingItems = groupPagingItems,
                                contentPadding = PaddingValues(
                                    bottom = innerPadding.calculateBottomPadding() + 12.dp
                                ),
                                onGroupClick = onGroupClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupList(
    modifier: Modifier,
    groupPagingItems: LazyPagingItems<GroupItemWithIntroInfo>,
    onGroupClick: (String) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(400.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = {
            items(
                count = groupPagingItems.itemCount,
                key = groupPagingItems.itemKey { it.id },
                contentType = groupPagingItems.itemContentType { "groupPagingItem" }) { index ->
                val group = groupPagingItems[index]
                SearchResultGroupItem(group = group, onClick = {
                    group?.let { onGroupClick(it.id) }
                })

            }
        },
    )
}