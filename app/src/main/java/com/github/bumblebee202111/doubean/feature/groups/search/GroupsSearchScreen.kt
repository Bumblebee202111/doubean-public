package com.github.bumblebee202111.doubean.feature.groups.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.ListItemGroupBinding
import com.github.bumblebee202111.doubean.feature.groups.common.groupsOfTheDay
import com.github.bumblebee202111.doubean.model.GroupSearchResultGroupItem
import com.github.bumblebee202111.doubean.model.RecommendedGroupItem

@Composable
fun GroupsSearchRoute(
    onGroupClick: (String) -> Unit,
    viewModel: GroupsSearchViewModel = hiltViewModel(),
) {
    val groupPagingItems = viewModel.results.collectAsLazyPagingItems()
    val groupsOfTheDay by viewModel.groupsOfTheDay.collectAsStateWithLifecycle()
    val shouldShowGroupsOfTheDay by viewModel.shouldShowGroupsOfTheDay.collectAsStateWithLifecycle()
    GroupsSearchScreen(
        groupPagingItems = groupPagingItems,
        groupsOfTheDay = groupsOfTheDay,
        shouldShowGroupsOfTheDay = shouldShowGroupsOfTheDay,
        setQuery = viewModel::setQuery,
        onGroupClick = onGroupClick
    )
}

@Composable
fun GroupsSearchScreen(
    groupPagingItems: LazyPagingItems<GroupSearchResultGroupItem>,
    groupsOfTheDay: List<RecommendedGroupItem>?,
    shouldShowGroupsOfTheDay: Boolean,
    setQuery: (originalInput: String) -> Unit,
    onGroupClick: (groupId: String) -> Unit,
) {
    Column {
        Spacer(
            Modifier.windowInsetsTopHeight(
                WindowInsets.statusBars
            )
        )
        SearchTextField(
            modifier = Modifier
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth()
        ) { query ->
            setQuery(query)
        }

        if (shouldShowGroupsOfTheDay) {
            groupsOfTheDay?.let {
                LazyColumn {
                    groupsOfTheDay(it, onGroupClick)
                }
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
fun SearchTextField(
    modifier: Modifier,
    onUpdateQuery: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var query by rememberSaveable { mutableStateOf("") }
    TextField(
        value = query,
        onValueChange = {
            query = it
            if (it.isBlank()) onUpdateQuery(it)
        },
        modifier = modifier,
        label = { Text(stringResource(id = R.string.search_hint)) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                onUpdateQuery(query)
            }
        ),
        singleLine = true
    )
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
                    setClickListener {
                        if (group != null) {
                            onGroupClick(group.id)
                        }
                    }
                    avatar.setContent {
                        AsyncImage(
                            model = group?.avatarUrl,
                            contentDescription = stringResource(id = R.string.a11y_group_item_image),
                            modifier = Modifier
                                .size(dimensionResource(id = R.dimen.icon_size_extra_large))
                                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_size_small))),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

            }
        },
    )
}