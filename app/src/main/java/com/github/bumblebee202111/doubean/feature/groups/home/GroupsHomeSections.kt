package com.github.bumblebee202111.doubean.feature.groups.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.GroupsSectionHeader
import com.github.bumblebee202111.doubean.feature.groups.shared.TopicItem
import com.github.bumblebee202111.doubean.feature.groups.shared.TopicItemDisplayMode
import com.github.bumblebee202111.doubean.feature.groups.shared.dayRankingItems
import com.github.bumblebee202111.doubean.model.groups.PinnedTabItem
import com.github.bumblebee202111.doubean.model.groups.SimpleGroup
import com.github.bumblebee202111.doubean.ui.component.InfoButton
import com.github.bumblebee202111.doubean.ui.component.InfoDialog
import com.github.bumblebee202111.doubean.ui.component.SectionErrorWithRetry
import com.github.bumblebee202111.doubean.ui.theme.DoubeanTheme
import java.util.Calendar

fun LazyListScope.itemSpacer() {
    item {
        Spacer(Modifier.height(16.dp))
    }
}

fun LazyListScope.myGroups(
    uiState: JoinedGroupsUiState,
    onGroupItemClick: (groupId: String) -> Unit,
    onRetryClick: () -> Unit,
) {
    item(key = "my_groups_header") {
        GroupsSectionHeader(title = stringResource(id = R.string.title_my_groups))
    }
    if (uiState.errorMessage != null) {
        item(key = "my_groups_error") {
            SectionErrorWithRetry(
                message = uiState.errorMessage.getString(),
                onRetryClick = onRetryClick
            )
        }
    }

    item(key = "my_groups_content") {
        when {
            uiState.isLoading && uiState.groups.isNullOrEmpty() -> Unit

            uiState.groups?.isEmpty() == true -> {
                Text(
                    text = stringResource(R.string.empty_my_groups),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            !uiState.groups.isNullOrEmpty() -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(items = uiState.groups, key = { group -> group.id }) { group ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            onClick = { onGroupItemClick(group.id) },
                            modifier = Modifier
                                .padding(4.dp)
                                .width(80.dp)
                        ) {
                            Column {
                                AsyncImage(
                                    group.avatar, contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                )
                                Text(
                                    text = group.name,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    textAlign = TextAlign.Left,
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun LazyListScope.dayRanking(
    uiState: DayRankingUiState,
    onGroupItemClick: (groupId: String) -> Unit,
    onRetryClick: () -> Unit,
) {
    when (uiState) {
        is DayRankingUiState.Error -> {
            item(key = "day_ranking_error") {
                GroupsSectionHeader(title = stringResource(id = R.string.title_day_ranking))
                SectionErrorWithRetry(
                    message = uiState.errorMessage.getString(),
                    onRetryClick = onRetryClick
                )
            }
        }

        is DayRankingUiState.Loading -> {
            item(key = "day_ranking_loading") {
                GroupsSectionHeader(title = stringResource(id = R.string.title_day_ranking))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        is DayRankingUiState.Success -> {
            item(key = "day_ranking_header") {
                GroupsSectionHeader(title = stringResource(id = R.string.title_day_ranking))
            }
            dayRankingItems(items = uiState.items, onItemClick = onGroupItemClick)
        }

        is DayRankingUiState.Hidden -> Unit
    }
}


fun LazyListScope.pinnedTabs(
    pinnedTabs: List<PinnedTabItem>,
    onItemClick: (groupId: String, tabId: String) -> Unit,
) {

    item(key = "pinned_tabs_header") {
        var showInfoDialog by remember { mutableStateOf(false) }

        if (showInfoDialog) {
            InfoDialog(
                onDismissRequest = { showInfoDialog = false },
                title = stringResource(R.string.title_pinned_items),
                text = stringResource(R.string.pin_tab_tooltip)
            )
        }

        GroupsSectionHeader(
            title = stringResource(id = R.string.title_pinned_items),
            infoButton = {
                InfoButton(
                    onClick = { showInfoDialog = true },
                    modifier = Modifier.size(24.dp),
                    contentDescription = stringResource(R.string.about_pinned_tabs)
                )
            }
        )
    }

    item(key = "pinned_tabs_content") {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(
                items = pinnedTabs,
                key = { tab -> listOf(tab.groupId, tab.tabId) }) { tab ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    onClick = { onItemClick(tab.groupId, tab.tabId) },
                    modifier = Modifier
                        .padding(4.dp)
                        .width(80.dp)
                ) {
                    Column {
                        AsyncImage(
                            tab.groupAvatar, contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.aspectRatio(1f)
                        )

                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Group,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = tab.groupName ?: "",
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp),
                                textAlign = TextAlign.Left,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                start = 4.dp,
                                end = 4.dp,
                                bottom = 4.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tab,
                                contentDescription = null,
                                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_extra_small)),
                            )
                            Text(
                                text = tab.tabName,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Left,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }

        }
    }
}

fun LazyListScope.myTopics(
    uiState: RecentTopicsFeedUiState,
    onTopicClick: (uri: String) -> Unit,
    onGroupClick: (groupId: String) -> Unit,
    onRetryClick: () -> Unit,
) {
    if (uiState.topics.isNullOrEmpty() && !uiState.isLoading && uiState.errorMessage == null) {
        return
    }

    item(key = "my_topics_header") {
        GroupsSectionHeader(title = stringResource(id = R.string.title_my_topics))
    }

    if (uiState.errorMessage != null) {
        item(key = "my_topics_error") {
            SectionErrorWithRetry(
                message = uiState.errorMessage.getString(),
                onRetryClick = onRetryClick
            )
        }
    }

    when {
        uiState.isLoading && uiState.topics.isNullOrEmpty() -> Unit


        !uiState.topics.isNullOrEmpty() -> {
            items(
                items = uiState.topics,
                key = { it.id },
                contentType = { "my_topics_item" }
            ) { topic ->
                TopicItem(
                    topicItemWithGroup = topic,
                    displayMode = TopicItemDisplayMode.SHOW_GROUP,
                    onTopicClick = onTopicClick,
                    onAuthorClick = onGroupClick,
                    onGroupClick = onGroupClick
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
@Preview(name = "Pinned Tabs Row", showBackground = true)
fun PinnedTabsPreview() {
    val mockData = listOf(
        PinnedTabItem(
            pinnedDate = Calendar.getInstance(),
            groupId = "1",
            groupName = "Sci-Fi Movie Geeks",
            groupAvatar = null,
            tabId = "tab1",
            tabName = "Spoiler Zone"
        ),
        PinnedTabItem(
            pinnedDate = Calendar.getInstance(),
            groupId = "2",
            groupName = "The International Society for Long Group Names",
            groupAvatar = null,
            tabId = "tab2",
            tabName = "Announcements & Very Long Tab Titles Go Here"
        ),
        PinnedTabItem(
            pinnedDate = Calendar.getInstance(),
            groupId = "3",
            groupName = "Book Club",
            groupAvatar = null,
            tabId = "tab3",
            tabName = "Chapter 5 Discussion"
        ),
        PinnedTabItem(
            pinnedDate = Calendar.getInstance(),
            groupId = "4",
            groupName = null,
            groupAvatar = null,
            tabId = "tab4",
            tabName = "Orphaned Tab"
        ),
    )

    DoubeanTheme {
        LazyColumn {
            pinnedTabs(
                pinnedTabs = mockData,
                onItemClick = { _, _ -> }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun MyGroupsPreview() {
    val mockData = JoinedGroupsUiState(
        groups = listOf(
            SimpleGroup(id = "1", name = "aa", url = "", uri = "", avatar = ""),
            SimpleGroup(id = "2", name = "bb", url = "", uri = "", avatar = ""),
            SimpleGroup(id = "3", name = "cc", url = "", uri = "", avatar = ""),
        )
    )
    LazyColumn {
        myGroups(uiState = mockData, onGroupItemClick = {}, onRetryClick = {})
    }
}
