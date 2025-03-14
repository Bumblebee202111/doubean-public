package com.github.bumblebee202111.doubean.feature.groups.resharestatuses

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
import com.github.bumblebee202111.doubean.model.GroupTopicCommentReshareItem
import com.github.bumblebee202111.doubean.ui.component.DateTimeText
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.util.intermediateDateTimeString

@Composable
fun ReshareStatusesScreen(
    onBackClick: () -> Unit,
    viewModel: ReshareStatusesViewModel = hiltViewModel(),
) {
    val reshareStatusLazyPagingItems =
        viewModel.reshareStatusesPagingData.collectAsLazyPagingItems()
    ReshareStatusesScreen(
        reshareStatusLazyPagingItems = reshareStatusLazyPagingItems,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReshareStatusesScreen(
    reshareStatusLazyPagingItems: LazyPagingItems<GroupTopicCommentReshareItem>,
    onBackClick: () -> Unit,
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
                    TopicActivityItemUserProfileImage(url = reshareStatus?.author?.avatarUrl)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserNameText(reshareStatus?.author?.name ?: "")
                            reshareStatus?.createTime?.let {
                                DateTimeText(
                                    modifier = Modifier.padding(start = 4.dp),
                                    text = it.intermediateDateTimeString(),
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