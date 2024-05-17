package com.github.bumblebee202111.doubean.feature.groups.groupsHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.ListItemRecommendedGroupBinding
import com.github.bumblebee202111.doubean.model.GroupFavoriteItem
import com.github.bumblebee202111.doubean.model.GroupItem
import com.github.bumblebee202111.doubean.model.RecommendedGroupItem
import com.github.bumblebee202111.doubean.ui.theme.AppTheme
import com.github.bumblebee202111.doubean.util.TopItemNoBackgroundUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint

class GroupsHomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?,
    ) = content {
        AppTheme {
            GroupsHomeScreen(
                viewModel = viewModel(),
                openSearch = { findNavController().navigate(R.id.nav_group_search) },
                openSettings = { findNavController().navigate(R.id.nav_settings) },
                openGroup = { groupId, tabId ->
                    findNavController().navigate(
                        GroupsHomeFragmentDirections.actionGroupsToGroupDetail(
                            groupId
                        ).setDefaultTabId(tabId)
                    )
                },
            )

        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun GroupsHomeScreen(
    viewModel: GroupsHomeViewModel,
    openSearch: () -> Unit,
    openSettings: () -> Unit,
    openGroup: (groupId: String, tabId: String?) -> Unit,
) {
    val joinedGroups by viewModel.joinedGroups.collectAsStateWithLifecycle()
    val following by viewModel.favorites.collectAsStateWithLifecycle()
    val groupsOfTheDay by viewModel.groupsOfTheDay.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = openSearch) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null
                        )
                    }
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Settings") }, onClick = openSettings)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(Modifier.padding(16.dp), contentPadding = innerPadding) {
            joinedGroups?.let { groups ->
                yourGroups(groups) { openGroup(it, null) }
            }
            following?.let { groups ->
                yourFollowing(groups, openGroup)
            }
            groupsOfTheDay?.let { groups ->
                groupsOfTheDay(groups) { openGroup(it, null) }

            }
        }

    }
}

fun LazyListScope.yourGroups(
    groups: List<GroupItem>,
    onGroupItemClick: (groupId: String) -> Unit,
) {
    item {
        Text(
            "My Groups",
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(items = groups, key = { group -> group.id }) { group ->
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
                            group.avatarUrl, contentDescription = null,
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

fun LazyListScope.yourFollowing(
    follows: List<GroupFavoriteItem>,
    onGroupItemClick: (groupId: String, tabId: String?) -> Unit,
) {
    item {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Favorites (Local Feature)",
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(items = follows, key = { group -> listOf(group.groupId, group.tabId) }) { group ->
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
                            group.groupAvatarUrl, contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.aspectRatio(1f)
                        )

                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_baseline_group),
                                null,
                                Modifier.size(16.dp)
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
                                    painter = painterResource(R.drawable.ic_baseline_tab),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
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

            }

        }
    }


}

private fun LazyListScope.groupsOfTheDay(
    recommendedGroups: List<RecommendedGroupItem>,
    onGroupItemClick: (groupId: String) -> Unit,
) {

    item {
        Column {
            Text(
                text = "Groups of the Day",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "Note: to be replaced by posts feed",
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }

    itemsIndexed(
        recommendedGroups,
        key = { _, recommendedGroup -> recommendedGroup.group.id }) { index, group ->
        AndroidViewBinding({ inflater, root, attachToRoot ->
            ListItemRecommendedGroupBinding.inflate(
                inflater,
                root,
                attachToRoot
            )
        }, update = fun ListItemRecommendedGroupBinding.() {
            val context = this.root.context
            val noColor = TopItemNoBackgroundUtil.getNoBackground(
                context,
                index,
                recommendedGroups.size
            )
            avatar.setContent {
                AsyncImage(
                    model = group.group.avatarUrl,
                    contentDescription = stringResource(id = R.string.a11y_group_item_image),
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.icon_size_extra_large))
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_size_small))),
                    contentScale = ContentScale.Crop
                )
            }
            this.noBackground = noColor
            this.recommendedGroup = group
            this.no.text = group.no.toString()
            this.clickListener = View.OnClickListener { onGroupItemClick(group.group.id) }
        })
    }


}


@Composable
@Preview(showBackground = true)
fun YourFollowingPreview(
) {
    val mockData = listOf(
        GroupFavoriteItem(Calendar.getInstance(), "1", "123", "wwww"),
        GroupFavoriteItem(Calendar.getInstance(), "12", "455234544444", "wwww"),
        GroupFavoriteItem(Calendar.getInstance(), "123", "6666", "wwww"),
        GroupFavoriteItem(Calendar.getInstance(), "1124", "", "wwww", "", "12345"),
    )
    LazyColumn {
        yourFollowing(mockData) { _, _ -> }
    }

}

@Composable
@Preview(showBackground = true)
private fun JoinedGroupsPreview() {
    val mockData = listOf(
        GroupItem("1345", "wwww", "avatar"),
        GroupItem("1234", "wwww", "avatar"),
        GroupItem("1534", "wwww", "avatar"),
        GroupItem("1324", "wwww", "avatar"),
    )
    LazyColumn {
        yourGroups(mockData) { }
    }
}