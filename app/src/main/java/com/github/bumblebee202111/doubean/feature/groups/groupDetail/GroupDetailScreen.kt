package com.github.bumblebee202111.doubean.feature.groups.groupDetail

import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.DialogContentGroupNotificationsPreferenceBinding
import com.github.bumblebee202111.doubean.databinding.LayoutGroupDetailBinding
import com.github.bumblebee202111.doubean.feature.groups.common.NotificationsButton
import com.github.bumblebee202111.doubean.feature.groups.common.TopicCountLimitEachFetchTextField
import com.github.bumblebee202111.doubean.feature.groups.groupTab.GroupTabScreen
import com.github.bumblebee202111.doubean.model.GroupDetail
import com.github.bumblebee202111.doubean.model.GroupMemberRole
import com.github.bumblebee202111.doubean.model.GroupTab
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.util.OpenInUtil
import com.github.bumblebee202111.doubean.util.ShareUtil
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
    val shouldDisplayFavoritedGroup: Boolean = viewModel.shouldDisplayFavoritedGroup
    val shouldDisplayUnfavoritedGroup = viewModel.shouldDisplayUnfavoritedGroup

    GroupDetailScreen(
        group = group,
        taggedTabs = taggedTabs,
        initialTabId = initialTabId,
        groupId = groupId,
        subscribeGroup = viewModel::subscribe,
        unsubscribeGroup = viewModel::unsubscribe,
        addFavorite = viewModel::addFavorite,
        removeFavorite = viewModel::removeFavorite,
        shouldDisplayFavoritedGroup = shouldDisplayFavoritedGroup,
        shouldDisplayUnfavoritedGroup = shouldDisplayUnfavoritedGroup,
        clearFavoritedGroupState = viewModel::clearFavoritedGroupState,
        clearUnfavoritedGroupState = viewModel::clearUnfavoritedGroupState,
        saveNotificationsPreference = viewModel::saveNotificationsPreference,
        onBackClick = onBackClick,
        onTopicClick = onTopicClick,
        onShowSnackbar = onShowSnackbar
    )
}


@Composable
fun GroupDetailScreen(
    group: GroupDetail?,
    taggedTabs: List<GroupTab>?,
    initialTabId: String?,
    groupId: String,
    shouldDisplayFavoritedGroup: Boolean,
    shouldDisplayUnfavoritedGroup: Boolean,
    subscribeGroup: () -> Unit,
    unsubscribeGroup: () -> Unit,
    addFavorite: () -> Unit,
    removeFavorite: () -> Unit,
    clearFavoritedGroupState: () -> Unit,
    clearUnfavoritedGroupState: () -> Unit,
    saveNotificationsPreference: (
        enableNotifications: Boolean,
        allowNotificationUpdates: Boolean,
        sortRecommendedTopicsBy: TopicSortBy,
        numberOfPostsLimitEachFeedFetch: Int,
    ) -> Unit,
    onBackClick: () -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
) {
    var openAlertDialog by remember { mutableStateOf(false) }
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

    Scaffold { paddingValues ->
        GroupDetailCoordinator(
            modifier = Modifier.padding(paddingValues),
            groupId = groupId,
            group = group,
            initialTabId = initialTabId,
            taggedTabs = taggedTabs,
            subscribeGroup = subscribeGroup,
            unsubscribeGroup = unsubscribeGroup,
            addFavorite = addFavorite,
            removeFavorite = removeFavorite,
            showNotificationsPrefDialog = { 
                openAlertDialog = true
            },
            onBackClick = onBackClick,
            onShareGroup = {
                val shareText = it.name + ' ' + it.shareUrl + "\r\n"
                ShareUtil.share(context, shareText)
            },
            viewInDouban = {
                OpenInUtil.openInDouban(context, it)
            },
            viewInBrowser = {
                OpenInUtil.openInBrowser(context, it)
            },
            onTopicClick = onTopicClick,
            onShowSnackbar = onShowSnackbar
        )
    }

    val enableNotifications = group?.enableNotifications
    val allowNotificationUpdates = group?.allowDuplicateNotifications
    val sortRecommendedTopicsBy = group?.sortRecommendedTopicsBy
    val numberOfTopicsLimitEachFeedFetch = group?.feedRequestTopicCountLimit

    if (openAlertDialog && enableNotifications != null && allowNotificationUpdates != null && sortRecommendedTopicsBy != null && numberOfTopicsLimitEachFeedFetch != null) {
        GroupNotificationsPreferenceDialog(
            initialEnableNotifications = enableNotifications,
            allowNotificationUpdates,
            sortRecommendedTopicsBy,
            numberOfTopicsLimitEachFeedFetch,
            onDismissRequest = { openAlertDialog = false }
        ) { enableNotificationsToSave, allowNotificationUpdatesToSave, sortRecommendedTopicsByToSave, numberOfTopicsLimitEachFeedFetchToSave ->
            saveNotificationsPreference(
                enableNotificationsToSave,
                allowNotificationUpdatesToSave,
                sortRecommendedTopicsByToSave,
                numberOfTopicsLimitEachFeedFetchToSave
            )
            openAlertDialog = false
        }
    }

}

@Composable
fun GroupNotificationsPreferenceDialog(
    initialEnableNotifications: Boolean,
    initialAllowNotificationUpdates: Boolean,
    initialSortRecommendedTopicsBy: TopicSortBy,
    initialNumberOfTopicsLimitEachFeedFetch: Int,
    onDismissRequest: () -> Unit,
    onConfirmation: (enableNotifications: Boolean, allowNotificationUpdates: Boolean, sortRecommendedTopicsBy: TopicSortBy, numberOfTopicsLimitEachFeedFetch: Int) -> Unit,
) {

    var enableNotifications by remember {
        mutableStateOf(initialEnableNotifications)
    }

    var allowNotificationUpdates by remember {
        mutableStateOf(initialAllowNotificationUpdates)
    }
    var sortRecommendedTopicsBy by remember {
        mutableStateOf(initialSortRecommendedTopicsBy)
    }
    var numberOfTopicsLimitEachFeedFetch by rememberSaveable {
        mutableIntStateOf(initialNumberOfTopicsLimitEachFeedFetch)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onConfirmation(
                    enableNotifications,
                    allowNotificationUpdates,
                    sortRecommendedTopicsBy,
                    numberOfTopicsLimitEachFeedFetch
                )
            }) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        title = { Text(text = stringResource(id = R.string.group_notifications_preference)) },
        text = {
            AndroidViewBinding(factory = { inflater, root, attachToRoot ->
                DialogContentGroupNotificationsPreferenceBinding.inflate(
                    inflater,
                    root,
                    attachToRoot
                ).apply {
                    sortRecommendedTopicsBySpinner.adapter = ArrayAdapter.createFromResource(
                        root.context,
                        R.array.sort_recommended_topics_by_array,
                        android.R.layout.simple_spinner_item
                    )
                        .apply { setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item) }
                }
            }) {

                fun getTopicSortByAt(spinnerItemPosition: Int) =
                    when (spinnerItemPosition) {
                        0 -> TopicSortBy.LAST_UPDATED
                        1 -> TopicSortBy.NEW_TOP
                        else -> throw java.lang.IndexOutOfBoundsException()
                    }

                fun getSpinnerItemPositionOf(topicSortBy: TopicSortBy) =
                    when (topicSortBy) {
                        TopicSortBy.LAST_UPDATED -> 0
                        TopicSortBy.NEW_TOP -> 1
                        else -> throw java.lang.IndexOutOfBoundsException()
                    }

                enableGroupNotificationsPref.apply {
                    isChecked = enableNotifications
                    setOnCheckedChangeListener { _, isChecked ->
                        enableNotifications = isChecked
                    }
                }
                allowDuplicateNotificationsPref.apply {
                    isEnabled = enableNotifications
                    isChecked = allowNotificationUpdates
                    setOnCheckedChangeListener { _, isChecked ->
                        allowNotificationUpdates = isChecked
                    }
                }
                sortRecommendedTopicsByTitle.isEnabled = enableNotifications
                sortRecommendedTopicsBySpinner.apply {
                    isEnabled = enableNotifications
                    setSelection(getSpinnerItemPositionOf(sortRecommendedTopicsBy))
                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long,
                        ) {
                            sortRecommendedTopicsBy = getTopicSortByAt(position)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                }
                feedRequestTopicCountLimitTitle.isEnabled = enableNotifications
                feedRequestTopicCountLimitTextField.setContent {
                    TopicCountLimitEachFetchTextField(
                        numberOfTopicsLimitEachFeedFetch = numberOfTopicsLimitEachFeedFetch,
                        onUpdateNumberOfTopicsLimitEachFeedFetch = {
                            numberOfTopicsLimitEachFeedFetch = it
                        },
                        enabled = enableNotifications
                    )
                }
            }

        }
    )
}

@Composable
fun GroupDetailCoordinator(
    modifier: Modifier = Modifier,
    groupId: String,
    group: GroupDetail?,
    initialTabId: String?,
    taggedTabs: List<GroupTab>?,
    subscribeGroup: () -> Unit,
    unsubscribeGroup: () -> Unit,
    addFavorite: () -> Unit,
    removeFavorite: () -> Unit,
    showNotificationsPrefDialog: () -> Unit,
    onBackClick: () -> Unit,
    onShareGroup: (group: GroupDetail) -> Unit,
    viewInDouban: (uriString: String) -> Unit,
    viewInBrowser: (urlString: String) -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
) {
    taggedTabs?.let {
        val pagerState = rememberPagerState(
            initialPage = taggedTabs.indexOfFirst { it.id == initialTabId } + 1,
            pageCount = { taggedTabs.size + 1 }
        )
        AndroidViewBinding(factory = LayoutGroupDetailBinding::inflate,
            modifier = modifier,
            onReset = {}) {
            val context = root.context

            notificationsButton.setContent {
                @Suppress("ConstantConditionIf") 
                if (false) {
                    group?.apply {
                        val memberRole = memberRole
                        val isSubscribed = isSubscribed
                        val isFavorited = group.isFavorited
                        if (isFavorited || isSubscribed == true || memberRole in setOf(
                                GroupMemberRole.MEMBER,
                                GroupMemberRole.MEMBER_ADMIN,
                            )
                        ) { 
                            NotificationsButton(
                                groupColor = group.color?.let {
                                    Color(it)
                                } ?: LocalContentColor.current,
                                enableNotifications = enableNotifications ?: false,
                                showPrefDialog = showNotificationsPrefDialog)
                        }
                    }
                }

            }

            joinButton.setContent {
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
                                colors = ButtonDefaults.buttonColors().run {
                                    group.color?.let {
                                        copy(containerColor = Color(it))
                                    } ?: this
                                }
                            ) {
                                Text(text = stringResource(id = R.string.subscribe))
                            }
                        }

                        isSubscriptionEnabled && isSubscribed == true -> {
                            OutlinedButton(
                                onClick = unsubscribeGroup,
                                colors = ButtonDefaults.outlinedButtonColors().run {
                                    group.color?.let {
                                        copy(contentColor = Color(it))
                                    } ?: this
                                }
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
                                enabled = false
                            ) {
                                Text(text = stringResource(id = R.string.joined))
                            }
                        }

                        
                    }
                }
            }

            toolbarLayout.setCollapsedTitleTextColor(context.getColor(R.color.doubean_white))
            appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                
                val shouldShowToolbar = verticalOffset + appBarLayout.totalScrollRange == 0
                appbar.isActivated = shouldShowToolbar
            }

            fun updateFavoriteMenuItem(favoriteMenuItem: MenuItem, isFavorited: Boolean) {
                when (isFavorited) {
                    true -> {
                        favoriteMenuItem.apply {
                            setTitle(R.string.unfavorite)
                            setIcon(R.drawable.ic_star)
                        }
                    }

                    false -> {
                        favoriteMenuItem.apply {
                            setTitle(R.string.favorite)
                            setIcon(R.drawable.ic_star_border)
                        }
                    }
                }
            }

            group?.let { group ->
                val favoriteMenuItem = toolbar.menu.findItem(R.id.action_favorite)
                updateFavoriteMenuItem(favoriteMenuItem, group.isFavorited)
                toolbar.setOnMenuItemClickListener { item ->
                    return@setOnMenuItemClickListener when (item.itemId) {
                        R.id.action_favorite -> {
                            when (group.isFavorited) {
                                true -> {
                                    removeFavorite()
                                }

                                false -> {
                                    addFavorite()
                                }
                            }
                            updateFavoriteMenuItem(
                                favoriteMenuItem,
                                isFavorited = !group.isFavorited
                            )
                            true
                        }

                        R.id.action_share -> {
                            onShareGroup(group)
                            true
                        }

                        R.id.action_view_in_douban -> {
                            viewInDouban(group.uri)
                            true
                        }

                        R.id.action_view_in_browser -> {
                            group.shareUrl?.let(viewInBrowser)
                            true
                        }

                        else -> {
                            false
                        }

                    }
                }
            }
            toolbar.setNavigationOnClickListener { onBackClick() }

            groupAvatar.setContent {
                AsyncImage(
                    model = group?.avatarUrl, contentDescription = null,
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.icon_size_extra_large))
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_size_small))),
                    contentScale = ContentScale.Crop
                )
            }

            tabRow.setContent {
                GroupTabRow(
                    pagerState = pagerState,
                    groupColor = group?.color,
                    taggedTabs = taggedTabs
                )
            }

            pager.setContent {
                GroupPager(
                    pagerState = pagerState,
                    taggedTabs = taggedTabs,
                    groupId = groupId,
                    group = group,
                    onTopicClick = onTopicClick,
                    onShowSnackbar = onShowSnackbar
                )
            }

            this@AndroidViewBinding.group = group
            group?.let { group ->
                group.color?.let { color ->
                    mask.setBackgroundColor(color)
                    toolbarLayout.setContentScrimColor(color)
                    toolbarLayout.setStatusBarScrimColor(color)
                }
            }
        }
    }
}

@Composable
fun GroupTabRow(pagerState: PagerState, taggedTabs: List<GroupTab>, groupColor: Int?) {
    val selectedTabIndex = pagerState.currentPage
    ScrollableTabRow(selectedTabIndex = selectedTabIndex,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            val modifier =
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
            groupColor?.let {
                TabRowDefaults.SecondaryIndicator(
                    color = Color(it),
                    modifier = modifier
                )
            } ?: TabRowDefaults.SecondaryIndicator(modifier = modifier)

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
) {
    HorizontalPager(state = pagerState,
        modifier = Modifier,
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
            onShowSnackbar = onShowSnackbar
        )
    }
}
