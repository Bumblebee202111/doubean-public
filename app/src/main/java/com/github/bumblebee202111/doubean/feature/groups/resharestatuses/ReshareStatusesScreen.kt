package com.github.bumblebee202111.doubean.feature.groups.resharestatuses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.TopicActivityItemUserProfileImage
import com.github.bumblebee202111.doubean.feature.statuses.UserNameText
import com.github.bumblebee202111.doubean.model.groups.GroupTopicCommentReshareItem
import com.github.bumblebee202111.doubean.ui.component.DateTimeText
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.util.intermediateDateTimeString

@Composable
fun ReshareStatusesScreen(
    onBackClick: () -> Unit,
    onUserClick: (id: String) -> Unit,
    viewModel: ReshareStatusesViewModel = hiltViewModel(),
) {
    val reshareStatusLazyPagingItems =
        viewModel.reshareStatusesPagingData.collectAsLazyPagingItems()
    ReshareStatusesScreen(
        reshareStatusLazyPagingItems = reshareStatusLazyPagingItems,
        onBackClick = onBackClick,
        onUserClick = onUserClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReshareStatusesScreen(
    reshareStatusLazyPagingItems: LazyPagingItems<GroupTopicCommentReshareItem>,
    onBackClick: () -> Unit,
    onUserClick: (id: String) -> Unit,
) {
    Scaffold(
        topBar = {
            DoubeanTopAppBar(
                titleResId = R.string.title_reshare_statuses,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
        ) {
            items(
                count = reshareStatusLazyPagingItems.itemCount,
                key = reshareStatusLazyPagingItems.itemKey { it.id },
                contentType = reshareStatusLazyPagingItems.itemContentType { "reshareStatus" }) { index ->
                val reshareStatus = reshareStatusLazyPagingItems[index]

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        ),
                ) {
                    TopicActivityItemUserProfileImage(
                        url = reshareStatus?.author?.avatar,
                        onClick = {
                            reshareStatus?.author?.let {
                                onUserClick(it.id)
                            }
                        })
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserNameText(
                                reshareStatus?.author?.name ?: "",
                                modifier = Modifier.clickable {
                                    reshareStatus?.author?.let {
                                        onUserClick(it.id)
                                    }
                                })
                            reshareStatus?.createTime?.let {
                                DateTimeText(
                                    text = it.intermediateDateTimeString(),
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                        Text(
                            text = reshareStatus?.text ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (index != reshareStatusLazyPagingItems.itemCount - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}