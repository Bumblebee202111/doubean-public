package com.github.bumblebee202111.doubean.feature.groups.groupdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TwoRowsTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.grouptab.GroupTabScreen
import com.github.bumblebee202111.doubean.feature.groups.shared.GroupNotificationPreferencesDialog
import com.github.bumblebee202111.doubean.feature.groups.shared.LargeGroupAvatar
import com.github.bumblebee202111.doubean.feature.groups.shared.groupTopAppBarColor
import com.github.bumblebee202111.doubean.model.GroupDetail
import com.github.bumblebee202111.doubean.model.GroupMemberRole
import com.github.bumblebee202111.doubean.model.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.GroupTab
import com.github.bumblebee202111.doubean.ui.component.ExpandCollapseText
import com.github.bumblebee202111.doubean.ui.component.doubeanTopAppBarHeight
import com.github.bumblebee202111.doubean.util.OpenInUtils
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.toColorOrPrimary
import kotlinx.coroutines.launch


@Composable
fun GroupDetailScreen(
    onBackClick: () -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    viewModel: GroupDetailViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String) -> Unit,
) {
    val taggedTabs: List<GroupTab>? by viewModel.tabs.collectAsStateWithLifecycle()
    val group: GroupDetail? by viewModel.group.collectAsStateWithLifecycle()
    val initialTabId: String? = viewModel.initialTabId
    val groupId: String = viewModel.groupId
    val defaultNotificationsPreference by viewModel.defaultNotificationPreferences.collectAsStateWithLifecycle()
    val shouldDisplayFavoritedGroup: Boolean = viewModel.shouldDisplayFavoritedGroup
    val shouldDisplayUnfavoritedGroup = viewModel.shouldDisplayUnfavoritedGroup

    GroupDetailScreen(
        group = group,
        taggedTabs = taggedTabs,
        initialTabId = initialTabId,
        groupId = groupId,
        defaultNotificationsPreference = defaultNotificationsPreference,
        subscribeGroup = viewModel::subscribe,
        unsubscribeGroup = viewModel::unsubscribe,
        addFavorite = viewModel::addFavorite,
        removeFavorite = viewModel::removeFavorite,
        shouldDisplayFavoritedGroup = shouldDisplayFavoritedGroup,
        shouldDisplayUnfavoritedGroup = shouldDisplayUnfavoritedGroup,
        clearFavoritedGroupState = viewModel::clearFavoritedGroupState,
        clearUnfavoritedGroupState = viewModel::clearUnfavoritedGroupState,
        saveNotificationsPreference = viewModel::saveNotificationPreferences,
        onBackClick = onBackClick,
        onTopicClick = onTopicClick,
        onShowSnackbar = onShowSnackbar
    )
}

/**
 * Representing a single group detail screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    group: GroupDetail?,
    taggedTabs: List<GroupTab>?,
    initialTabId: String?,
    groupId: String,
    defaultNotificationsPreference: GroupNotificationPreferences?,
    shouldDisplayFavoritedGroup: Boolean,
    shouldDisplayUnfavoritedGroup: Boolean,
    subscribeGroup: () -> Unit,
    unsubscribeGroup: () -> Unit,
    addFavorite: () -> Unit,
    removeFavorite: () -> Unit,
    clearFavoritedGroupState: () -> Unit,
    clearUnfavoritedGroupState: () -> Unit,
    saveNotificationsPreference: (preference: GroupNotificationPreferences) -> Unit,
    onBackClick: () -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
) {

    var openNotificationsPreferenceDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val favoritedGroupMessage = stringResource(id = R.string.favorited_group)

    LaunchedEffect(key1 = shouldDisplayFavoritedGroup) {
        if (shouldDisplayFavoritedGroup) {
            onShowSnackbar(favoritedGroupMessage)
            clearFavoritedGroupState()
        }
    }

    val unfavoritedGroupMessage = stringResource(id = R.string.unfavorited_group)

    LaunchedEffect(key1 = shouldDisplayUnfavoritedGroup) {
        if (shouldDisplayUnfavoritedGroup) {
            onShowSnackbar(unfavoritedGroupMessage)
            clearUnfavoritedGroupState()
        }
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            GroupDetailTopBar(
                group = group,
                scrollBehavior = scrollBehavior,
                showNotificationsPrefDialog = {
                    openNotificationsPreferenceDialog = true
                },
                addFavorite = addFavorite, removeFavorite = removeFavorite,
                subscribeGroup = subscribeGroup,
                unsubscribeGroup = unsubscribeGroup,
                onShareGroup = {
                    val shareText = it.name + ' ' + it.shareUrl + "\r\n"
                    ShareUtil.share(context, shareText)
                },
                viewInDouban = { OpenInUtils.openInDouban(context, it) },
                viewInBrowser = { OpenInUtils.openInBrowser(context, it) },
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->

        if (taggedTabs != null) {
            val pagerState = rememberPagerState(
                initialPage = taggedTabs.indexOfFirst { it.id == initialTabId } + 1,
                pageCount = { taggedTabs.size + 1 }
            )
            Column(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
            ) {
                GroupTabRow(
                    pagerState = pagerState,
                    groupColor = group?.color,
                    taggedTabs = taggedTabs
                )
                GroupPager(
                    pagerState = pagerState,
                    taggedTabs = taggedTabs,
                    groupId = groupId,
                    group = group,
                    onTopicClick = onTopicClick,
                    onShowSnackbar = onShowSnackbar,
                    tabContentPadding = PaddingValues(
                        bottom = innerPadding.calculateBottomPadding()
                    )

                )
            }

        }
    }
    if (group != null && defaultNotificationsPreference != null) {

        if (openNotificationsPreferenceDialog) {
            GroupNotificationPreferencesDialog(
                titleTextResId = R.string.group_notification_preferences,
                initialPreference = group.notificationPreferences ?: defaultNotificationsPreference,
                onDismissRequest = { openNotificationsPreferenceDialog = false }
            ) { preferenceToSave ->
                saveNotificationsPreference(preferenceToSave)
                openNotificationsPreferenceDialog = false
            }
        }
    }


}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailTopBar(
    group: GroupDetail?,
    scrollBehavior: TopAppBarScrollBehavior,
    showNotificationsPrefDialog: () -> Unit,
    addFavorite: () -> Unit,
    removeFavorite: () -> Unit,
    subscribeGroup: () -> Unit,
    unsubscribeGroup: () -> Unit,
    onShareGroup: (group: GroupDetail) -> Unit,
    viewInDouban: (uriString: String) -> Unit,
    viewInBrowser: (urlString: String) -> Unit,
    onBackClick: () -> Unit,
) {

    val groupColor = group?.color.toColorOrPrimary()
    TwoRowsTopAppBar(
        title = { expanded ->
            if (expanded) {
                Column(modifier = Modifier.padding(end = 16.dp, bottom = 12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            LargeGroupAvatar(avatarUrl = group?.avatarUrl)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = group?.name ?: "",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = group?.let { it.memberCount.toString() + it.memberName }
                                    ?: "",
                                style = MaterialTheme.typography.bodyMedium
                            )

                        }
                        Row {
                            group?.apply {
                                val memberRole = memberRole
                                val isSubscribed = isSubscribed

                                if (notificationPreferences?.notificationsEnabled == true || isFavorited || isSubscribed == true || memberRole in setOf(
                                        GroupMemberRole.MEMBER,
                                        GroupMemberRole.MEMBER_ADMIN,
                                    )
                                ) { // only shown when its your group or db record exists with notificationsEnabled being true
                                    GroupNotificationsButton(
                                        groupColor = groupColor,
                                        notificationsEnabled = notificationPreferences?.notificationsEnabled
                                            ?: false,
                                        onOpenPreferencesDialog = showNotificationsPrefDialog
                                    )
                                }
                            }

                            group?.apply {
                                val isSubscriptionEnabled = isSubscriptionEnabled ?: return@apply
                                val memberRole = memberRole ?: return@apply
                                val isSubscribed = isSubscribed

                                when {
                                    isSubscriptionEnabled && isSubscribed == false && memberRole in setOf(
                                        GroupMemberRole.NOT_MEMBER,
                                        GroupMemberRole.MEMBER_INVITED,
                                        GroupMemberRole.MEMBER_INVITED_WAIT_FOR_ADMIN,
                                        GroupMemberRole.MEMBER_REQUESTED_WAIT_FOR_ADMIN
                                    ) -> {
                                        Button(
                                            onClick = subscribeGroup,
                                            colors = ButtonDefaults.buttonColors().copy(
                                                contentColor = groupColor,
                                                containerColor = Color.White
                                            )
                                        ) {
                                            Text(text = stringResource(id = R.string.subscribe))
                                        }
                                    }

                                    isSubscriptionEnabled && isSubscribed == true -> {
                                        OutlinedButton(
                                            onClick = unsubscribeGroup,
                                            colors = ButtonDefaults.outlinedButtonColors().copy(
                                                contentColor = Color.White,
                                                containerColor = groupColor
                                            )
                                        ) {
                                            Text(text = stringResource(id = R.string.subscribed))
                                        }
                                    }

                                    memberRole in setOf(
                                        GroupMemberRole.MEMBER,
                                        GroupMemberRole.MEMBER_ADMIN,
                                    ) -> {
                                        TextButton(
                                            onClick = {},
                                            colors = ButtonDefaults.textButtonColors().copy(
                                                disabledContentColor = Color.White,
                                                disabledContainerColor = groupColor
                                            ),
                                            enabled = false
                                        ) {
                                            Text(text = stringResource(id = R.string.joined))
                                        }
                                    }

                                    //More cases: 已被本组封禁/...
                                }
                            }
                        }
                    }
                    ExpandCollapseText(
                        text = group?.description ?: "",
                        maxLines = 2,
                        style = MaterialTheme.typography.bodyMedium,
                        usesPrimaryLinkSpanStyle = false
                    )
                }
            } else {
                Column {
                    Text(text = group?.name ?: "")
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            if (group != null) {
                IconButton(
                    onClick = if (group.isFavorited) removeFavorite else addFavorite
                ) {
                    Icon(
                        imageVector = if (group.isFavorited) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null
                    )
                }
            }
            var moreExpanded by remember { mutableStateOf(false) }
            var viewInExpanded by remember { mutableStateOf(false) }
            IconButton(onClick = { moreExpanded = !moreExpanded }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null
                )
            }
            DropdownMenu(expanded = moreExpanded, onDismissRequest = { moreExpanded = false }) {
                if (group != null) {
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.share)) },
                        onClick = {
                            moreExpanded = false
                            onShareGroup(group)
                        }
                    )
                }
                DropdownMenuItem(text = {
                    Text(text = stringResource(R.string.view_in))
                },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        moreExpanded = false
                        viewInExpanded = true
                    })
            }
            if (group != null) {
                DropdownMenu(
                    expanded = viewInExpanded,
                    onDismissRequest = { viewInExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.view_in_douban)) },
                        onClick = { viewInDouban(group.uri) })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.view_in_browser)) },
                        onClick = {
                            viewInBrowser(group.url)
                        })

                }
            }

        },
        scrollBehavior = scrollBehavior,
        collapsedHeight = doubeanTopAppBarHeight,
        colors = groupTopAppBarColor(groupColor)
    )
}


@Composable
fun GroupTabRow(
    pagerState: PagerState,
    taggedTabs: List<GroupTab>,
    groupColor: String?,
    modifier: Modifier = Modifier,
) {
    val selectedTabIndex = pagerState.currentPage
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        edgePadding = 0.dp,
        indicator = {
            val indicatorModifier =
                Modifier.tabIndicatorOffset(selectedTabIndex)
                TabRowDefaults.SecondaryIndicator(
                    color = groupColor.toColorOrPrimary(),
                    modifier = indicatorModifier
                )
        },
        divider = {}
    ) {
        val coroutineScope = rememberCoroutineScope()

        Tab(
            selected = pagerState.currentPage == 0,
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
            },
            text = {
                Text(text = stringResource(id = R.string.all))
            }
        )

        taggedTabs.forEachIndexed { index, groupTab ->

            val title = groupTab.name
            Tab(
                selected = pagerState.currentPage == index + 1,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index + 1)
                    }
                },
                text = {
                    if (title != null) {
                        Text(text = title)
                    }
                }
            )
        }
    }
}

@Composable
fun GroupPager(
    pagerState: PagerState,
    taggedTabs: List<GroupTab>,
    groupId: String,
    group: GroupDetail?,
    onTopicClick: (topicId: String) -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
    modifier: Modifier = Modifier,
    tabContentPadding: PaddingValues = PaddingValues(),
) {
    HorizontalPager(state = pagerState,
        modifier = modifier,
        key = { index ->
            when (index) {
                0 -> ""
                else -> taggedTabs[index - 1].id
            }
        }) { page ->
        val tabId = when (page) {
            0 -> null
            else -> taggedTabs[page - 1].id
        }
        GroupTabScreen(
            groupId = groupId,
            tabId = tabId,
            group = group,
            onTopicClick = onTopicClick,
            onShowSnackbar = onShowSnackbar,
            contentPadding = tabContentPadding
        )
    }
}

@Composable
private fun GroupNotificationsButton(
    groupColor: Color,
    notificationsEnabled: Boolean,
    onOpenPreferencesDialog: () -> Unit,
) {
    OutlinedIconButton(
        onClick = onOpenPreferencesDialog,
        colors = IconButtonDefaults.outlinedIconButtonColors(
            contentColor = Color.White,
            containerColor = groupColor
        )
    ) {
        Icon(
            imageVector = if (notificationsEnabled) {
                Icons.Filled.Notifications
            } else {
                Icons.Filled.NotificationAdd
            },
            contentDescription = null
        )
    }
}