package com.github.bumblebee202111.doubean.feature.groups.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.isVisible
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.ListItemGroupBinding
import com.github.bumblebee202111.doubean.feature.groups.common.groupsOfTheDay
import com.github.bumblebee202111.doubean.model.GroupSearchResultGroupItem
import com.github.bumblebee202111.doubean.model.RecommendedGroupItem
import com.github.bumblebee202111.doubean.ui.LargeGroupAvatar
import com.github.bumblebee202111.doubean.ui.component.SearchTextField

@Composable
fun GroupsSearchScreen(
    onGroupClick: (String) -> Unit,
    viewModel: GroupsSearchViewModel = hiltViewModel(),
) {
    val groupPagingItems = viewModel.results.collectAsLazyPagingItems()
    val groupsOfTheDay by viewModel.groupsOfTheDay.collectAsStateWithLifecycle()
    GroupsSearchScreen(
        groupPagingItems = groupPagingItems,
        groupsOfTheDay = groupsOfTheDay,
        onQueryChange = viewModel::onQueryChange,
        onSearchTriggered = viewModel::onSearchTriggered,
        onGroupClick = onGroupClick
    )
}

@Composable
fun GroupsSearchScreen(
    groupPagingItems: LazyPagingItems<GroupSearchResultGroupItem>,
    groupsOfTheDay: List<RecommendedGroupItem>?,
    onQueryChange: (String) -> Unit,
    onSearchTriggered: (originalInput: String) -> Unit,
    onGroupClick: (groupId: String) -> Unit,
) {
    Column {
        Spacer(
            Modifier.windowInsetsTopHeight(
                WindowInsets.statusBars
            )
        )
        SearchTextField(
            labelTextResId = R.string.search_groups_hint,
            modifier = Modifier
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth(),
            onQueryChange = onQueryChange,
            onSearchTriggered = onSearchTriggered
        )

        if (groupsOfTheDay != null) {
            LazyColumn {
                groupsOfTheDay(groupsOfTheDay, onGroupClick)
            }
        } else {
            GroupList(
                modifier = Modifier.padding(vertical = 8.dp),
                groupPagingItems = groupPagingItems,
                onGroupClick = onGroupClick
            )
        }
    }
}

@Composable
fun GroupList(
    modifier: Modifier,
    groupPagingItems: LazyPagingItems<GroupSearchResultGroupItem>,
    onGroupClick: (String) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(400.dp),
        modifier = modifier.fillMaxSize(),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(
                count = groupPagingItems.itemCount,
                key = groupPagingItems.itemKey { it.id },
                contentType = groupPagingItems.itemContentType { "groupPagingItem" }) { index ->
                val group = groupPagingItems[index]
                AndroidViewBinding(
                    factory = ListItemGroupBinding::inflate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    onReset = {}) {
                    this.group = group
                    root.isVisible = group != null
                    setClickListener {
                        if (group != null) {
                            onGroupClick(group.id)
                        }
                    }
                    avatar.setContent {
                        LargeGroupAvatar(avatarUrl = group?.avatarUrl)
                    }
                }

            }
        },
    )
}