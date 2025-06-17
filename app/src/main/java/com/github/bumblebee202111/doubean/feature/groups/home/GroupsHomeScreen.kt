package com.github.bumblebee202111.doubean.feature.groups.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.TopicItem
import com.github.bumblebee202111.doubean.feature.groups.shared.TopicItemDisplayMode
import com.github.bumblebee202111.doubean.feature.groups.shared.dayRanking
import com.github.bumblebee202111.doubean.model.fangorns.User
import com.github.bumblebee202111.doubean.model.groups.GroupFavoriteItem
import com.github.bumblebee202111.doubean.model.groups.SimpleGroup
import com.github.bumblebee202111.doubean.model.groups.TopicItemWithGroup
import com.github.bumblebee202111.doubean.ui.common.AppBarNavigationAvatar
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import java.util.Calendar

@Composable
fun GroupsHomeScreen(
    viewModel: GroupsHomeViewModel = hiltViewModel(),
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onTopicClick: (topicId: String) -> Unit,
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val joinedGroupsUiState by viewModel.joinedGroupsUiState.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val dayRankingUiState by viewModel.dayRankingUiState.collectAsStateWithLifecycle()
    val recentTopicsFeed by viewModel.recentTopicsFeed.collectAsStateWithLifecycle()
    GroupsHomeScreen(
        currentUser = currentUser,
        joinedGroupsUiState = joinedGroupsUiState,
        favorites = favorites,
        dayRankingUiState = dayRankingUiState,
        recentTopicsFeed = recentTopicsFeed,
        onAvatarClick = onAvatarClick,
        onSearchClick = onSearchClick,
        onNotificationsClick = onNotificationsClick,
        onGroupClick = onGroupClick,
        onTopicClick = onTopicClick
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun GroupsHomeScreen(
    currentUser: User?,
    joinedGroupsUiState: JoinedGroupsUiState,
    favorites: List<GroupFavoriteItem>?,
    dayRankingUiState: DayRankingUiState,
    recentTopicsFeed: List<TopicItemWithGroup>?,
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onGroupClick: (String, String?) -> Unit,
    onTopicClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            DoubeanTopAppBar(
                navigationIcon = {
                    AppBarNavigationAvatar(
                        currentUser = currentUser,
                        onAvatarClick = onAvatarClick
                    )
                },
                title = {},
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(
                    LayoutDirection.Ltr
                ),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
            )
        ) {
            joinedGroupsUiState.groups?.let { groups ->
                myGroups(groups) { onGroupClick(it, null) }
            }
            favorites?.takeIf(List<GroupFavoriteItem>::isNotEmpty)?.let { groups ->
                favorites(groups, onGroupClick)
            }
            if (dayRankingUiState is DayRankingUiState.Success) {
                this.dayRanking(dayRankingUiState.items) { onGroupClick(it, null) }
            }

            recentTopicsFeed?.let { topic ->
                myTopics(
                    topics = topic,
                    onTopicClick = onTopicClick,
                    onGroupClick = { groupId -> onGroupClick(groupId, null) })
            }
        }
    }
}

private fun LazyListScope.myGroups(
    groups: List<SimpleGroup>,
    onGroupItemClick: (groupId: String) -> Unit,
) {
    item(contentType = "myGroups") {
        Text(
            stringResource(id = R.string.title_my_groups),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.size(4.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items = groups, key = { group -> group.id }) { group ->
                if (groups.first() == group) {
                    Spacer(modifier = Modifier.size(16.dp))
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ), border = BorderStroke(1.dp, Color.Black),
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
                if (groups.last() == group) {
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }
        Spacer(Modifier.size(16.dp))
    }

}

private fun LazyListScope.favorites(
    follows: List<GroupFavoriteItem>,
    onGroupItemClick: (groupId: String, tabId: String?) -> Unit,
) {
    item(contentType = "favorites") {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(id = R.string.title_favorites_local_feature),
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items = follows, key = { group -> listOf(group.groupId, group.tabId) }) { group ->
                if (follows.first() == group) {
                    Spacer(modifier = Modifier.size(16.dp))
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ), border = BorderStroke(1.dp, Color.Black),
                    onClick = { onGroupItemClick(group.groupId, group.tabId) },
                    modifier = Modifier
                        .padding(4.dp)
                        .width(80.dp)
                ) {
                    Column {
                        AsyncImage(
                            group.groupAvatar, contentDescription = null,
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
                                text = group.groupName ?: "",
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp),
                                textAlign = TextAlign.Left,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                            )
                        }

                        group.tabId?.let {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Tab,
                                    contentDescription = null,
                                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_extra_small)),
                                )
                                Text(
                                    text = group.groupTabName ?: "",
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

                if (follows.last() == group) {
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }

        }
        Spacer(Modifier.size(16.dp))
    }
}

private fun LazyListScope.myTopics(
    topics: List<TopicItemWithGroup>,
    onTopicClick: (topicId: String) -> Unit,
    onGroupClick: (groupId: String) -> Unit,
) {
    item(contentType = "myTopicsTitle") {
        Text(
            text = stringResource(R.string.title_my_topics),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.size(4.dp))
    }

    itemsIndexed(
        items = topics,
        key = { _, topic -> topic.id },
        contentType = { _, _ -> "topicItem" }) { index, item ->
        if (index != 0) {
            HorizontalDivider()
        }
        TopicItem(
            topicItemWithGroup = item,
            displayMode = TopicItemDisplayMode.SHOW_GROUP,
            onTopicClick = onTopicClick,
            onGroupClick = onGroupClick
        )
    }
}

@Composable
@Preview(showBackground = true)
fun MyGroupsPreview(
) {
    val mockData = listOf(
        GroupFavoriteItem(Calendar.getInstance(), "1", "123", "wwww"),
        GroupFavoriteItem(Calendar.getInstance(), "12", "455234544444", "wwww"),
        GroupFavoriteItem(Calendar.getInstance(), "123", "6666", "wwww"),
        GroupFavoriteItem(Calendar.getInstance(), "1124", "", "wwww", "", "12345"),
    )
    LazyColumn {
        favorites(mockData) { _, _ -> }
    }

}

@Composable
@Preview(showBackground = true)
private fun JoinedGroupsPreview() {
    val mockData = listOf(
        SimpleGroup(id = "1", name = "aa", url = "", uri = "", avatar = ""),
        SimpleGroup(id = "2", name = "bb", url = "", uri = "", avatar = ""),
        SimpleGroup(id = "3", name = "cc", url = "", uri = "", avatar = ""),
    )
    LazyColumn {
        myGroups(mockData) { }
    }
}