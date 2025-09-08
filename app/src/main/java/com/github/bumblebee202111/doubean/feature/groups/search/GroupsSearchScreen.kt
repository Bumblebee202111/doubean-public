package com.github.bumblebee202111.doubean.feature.groups.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.SearchResultGroupItem
import com.github.bumblebee202111.doubean.feature.groups.shared.dayRanking
import com.github.bumblebee202111.doubean.feature.search.common.SearchHistory
import com.github.bumblebee202111.doubean.feature.search.common.SearchHistoryList
import com.github.bumblebee202111.doubean.model.groups.GroupItemWithIntroInfo
import com.github.bumblebee202111.doubean.ui.component.DoubeanAppBarWithSearch

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
        onHistoryClick = viewModel::onQueryChange,
        onDeleteHistoryItem = viewModel::onDeleteHistoryItem,
        onClearHistory = viewModel::onClearHistory,
        onGroupClick = onGroupClick,
        onBackClick = onBackClick
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
    onHistoryClick: (String) -> Unit,
    onDeleteHistoryItem: (String) -> Unit,
    onClearHistory: () -> Unit,
    onGroupClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    var isSearchFocused by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            DoubeanAppBarWithSearch(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearchTriggered,
                onBackClick = onBackClick,
                onFocusChanged = { isSearchFocused = it },
                placeholderText = stringResource(R.string.search_groups_hint)
            )
        }) { innerPadding ->
        when {
            query.isBlank() && isSearchFocused -> {
                SearchHistoryList(
                    modifier = Modifier.padding(innerPadding),
                    history = history,
                    onHistoryClick = onHistoryClick,
                    onDeleteClick = onDeleteHistoryItem,
                    onClearAllClick = onClearHistory
                )
            }

            query.isBlank() -> {
                if (dayRankingUiState is DayRankingUiState.Success) {
                    LazyColumn(
                        contentPadding = innerPadding
                    ) {
                        dayRanking(dayRankingUiState.items, onGroupClick)
                    }
                }
            }

            else -> {
                GroupList(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    groupPagingItems = groupPagingItems,
                    contentPadding = innerPadding,
                    onGroupClick = onGroupClick
                )
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