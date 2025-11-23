package com.github.bumblebee202111.doubean.feature.groups.groupdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.tab.GroupTab
import com.github.bumblebee202111.doubean.feature.groups.shared.GroupNotificationPreferencesDialog
import com.github.bumblebee202111.doubean.feature.groups.shared.LargeGroupAvatar
import com.github.bumblebee202111.doubean.feature.groups.shared.groupTopAppBarColor
import com.github.bumblebee202111.doubean.model.groups.GroupDetail
import com.github.bumblebee202111.doubean.model.groups.GroupMemberRole
import com.github.bumblebee202111.doubean.model.groups.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.groups.GroupTab
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator
import com.github.bumblebee202111.doubean.ui.component.MoreButton
import com.github.bumblebee202111.doubean.ui.component.doubeanExpandedTopBarContentPadding
import com.github.bumblebee202111.doubean.ui.component.doubeanTopAppBarHeight
import com.github.bumblebee202111.doubean.util.OpenInUtils
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.toColorOrPrimary
import kotlinx.coroutines.launch


@Composable
fun GroupDetailScreen(
    onBackClick: () -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    viewModel: GroupDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val initialTabId: String? = viewModel.initialTabId
    val groupId: String = viewModel.groupId

    GroupDetailScreen(
        uiState = uiState,
        initialTabId = initialTabId,
        groupId = groupId,
        subscribeGroup = viewModel::subscribe,
        unsubscribeGroup = viewModel::unsubscribe,
        saveNotificationsPreference = viewModel::saveNotificationPreferences,
        onBackClick = onBackClick,
        onTopicClick = onTopicClick,
        onUserClick = onUserClick,
        onRetryClick = viewModel::retry
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    uiState: GroupDetailUiState,
    initialTabId: String?,
    groupId: String,
    subscribeGroup: () -> Unit,
    unsubscribeGroup: () -> Unit,
    saveNotificationsPreference: (preference: GroupNotificationPreferences) -> Unit,
    onBackClick: () -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onRetryClick: () -> Unit,
) {

    var openNotificationsPreferenceDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            GroupDescriptionSheetContent(
                group = uiState.group,
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }
            )
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            GroupDetailTopBar(
                uiState = uiState,
                scrollBehavior = scrollBehavior,
                showNotificationsPrefDialog = {
                    openNotificationsPreferenceDialog = true
                },
                subscribeGroup = subscribeGroup,
                unsubscribeGroup = unsubscribeGroup,
                onShareGroup = {
                    val shareText = it.name + ' ' + it.sharingUrl + "\r\n"
                    ShareUtil.share(context, shareText)
                },
                viewInDouban = { OpenInUtils.openInDouban(context, it) },
                viewInBrowser = { OpenInUtils.openInBrowser(context, it) },
                onBackClick = onBackClick,
                onDescriptionClick = { showBottomSheet = true },
            )
        }
    ) { innerPadding ->
        val group = uiState.group
        val notificationPreferences = uiState.notificationPreferences
        when {
            group != null -> {
                val taggedTabs = group.tabs
                val pagerState = rememberPagerState(
                    initialPage = taggedTabs.indexOfFirst { it.id == initialTabId } + 1,
                    pageCount = { taggedTabs.size + 1 }
                )
                val isPullToRefreshEnabled = scrollBehavior.state.heightOffset == 0f

                Column(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                ) {
                    GroupTabRow(
                        pagerState = pagerState,
                        groupColor = group.color,
                        taggedTabs = taggedTabs
                    )
                    GroupPager(
                        pagerState = pagerState,
                        taggedTabs = taggedTabs,
                        groupId = groupId,
                        group = group,
                        isPullToRefreshEnabled = isPullToRefreshEnabled,
                        onTopicClick = onTopicClick,
                        onUserClick = onUserClick,
                        tabContentPadding = PaddingValues(
                            bottom = innerPadding.calculateBottomPadding()
                        )

                    )

                }

                if (notificationPreferences != null && openNotificationsPreferenceDialog) {
                    GroupNotificationPreferencesDialog(
                        titleTextResId = R.string.group_notification_preferences,
                        initialPreference = notificationPreferences,
                        onDismissRequest = { openNotificationsPreferenceDialog = false }
                    ) { preferenceToSave ->
                        saveNotificationsPreference(preferenceToSave)
                        openNotificationsPreferenceDialog = false
                    }
                }
            }

            uiState.isLoading -> {
                FullScreenLoadingIndicator(contentPadding = innerPadding)
            }

            uiState.errorMessage != null -> {
                FullScreenErrorWithRetry(
                    message = uiState.errorMessage.getString(),
                    contentPadding = innerPadding,
                    onRetryClick = onRetryClick
                )
            }

        }

    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailTopBar(
    uiState: GroupDetailUiState,
    scrollBehavior: TopAppBarScrollBehavior,
    showNotificationsPrefDialog: () -> Unit,
    subscribeGroup: () -> Unit,
    unsubscribeGroup: () -> Unit,
    onShareGroup: (GroupDetail) -> Unit,
    viewInDouban: (String) -> Unit,
    viewInBrowser: (String) -> Unit,
    onBackClick: () -> Unit,
    onDescriptionClick: () -> Unit,
) {
    val group = uiState.group
    val cachedGroup = uiState.cachedGroup
    val notificationPreferences = uiState.notificationPreferences
    val groupColor = (group?.color ?: cachedGroup?.color).toColorOrPrimary()
    TwoRowsTopAppBar(
        title = { expanded ->
            if (expanded) {
                Column(modifier = Modifier.padding(doubeanExpandedTopBarContentPadding)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            LargeGroupAvatar(avatarUrl = group?.avatar ?: cachedGroup?.avatar)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = group?.name ?: cachedGroup?.name ?: "",
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

                                if (notificationPreferences?.notificationsEnabled == true || isSubscribed == true || memberRole in setOf(
                                        GroupMemberRole.MEMBER,
                                        GroupMemberRole.MEMBER_ADMIN,
                                    )
                                ) {
                                    GroupNotificationsButton(
                                        groupColor = groupColor,
                                        notificationsEnabled = notificationPreferences?.notificationsEnabled == true,
                                        onOpenPreferencesDialog = showNotificationsPrefDialog
                                    )
                                }

                                val isSubscriptionEnabled = isSubscriptionEnabled ?: return@apply
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
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color.White,
                                                containerColor = groupColor
                                            ),
                                            border = BorderStroke(
                                                width = 1.dp,
                                                color = Color.White
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


                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                            .clickable { onDescriptionClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = group?.description ?: "",
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            } else {
                Column {
                    Text(text = group?.name ?: "")
                }
            }
        },
        navigationIcon = {
            BackButton(onClick = onBackClick)
        },
        actions = {
            var moreExpanded by remember { mutableStateOf(false) }
            var viewInExpanded by remember { mutableStateOf(false) }
            MoreButton(onClick = { moreExpanded = !moreExpanded })
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
                DropdownMenuItem(
                    text = {
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
        divider = {},
        minTabWidth = 0.dp
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
    isPullToRefreshEnabled: Boolean,
    onTopicClick: (topicId: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    modifier: Modifier = Modifier,
    tabContentPadding: PaddingValues = PaddingValues(),
) {
    HorizontalPager(
        state = pagerState,
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
        GroupTab(
            groupId = groupId,
            tabId = tabId,
            group = group,
            isPullToRefreshEnabled = isPullToRefreshEnabled,
            onTopicClick = onTopicClick,
            onUserClick = onUserClick,
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

@Composable
private fun GroupDescriptionSheetContent(
    group: GroupDetail?,
    onClose: () -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.group_description_title),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.content_description_close)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(group?.description ?: "", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(32.dp))
    }
}