package com.github.bumblebee202111.doubean.feature.groups.groupTab

import android.content.res.ColorStateList
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.DialogContentGroupTabNotificationsPreferenceBinding
import com.github.bumblebee202111.doubean.databinding.ViewGroupTabActionsBinding
import com.github.bumblebee202111.doubean.feature.groups.common.NotificationsButton
import com.github.bumblebee202111.doubean.feature.groups.common.TopicCountLimitEachFetchTextField
import com.github.bumblebee202111.doubean.model.GroupDetail
import com.github.bumblebee202111.doubean.model.TopicItem
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.model.toItem
import com.github.bumblebee202111.doubean.ui.SortTopicsBySpinner
import com.github.bumblebee202111.doubean.ui.TopicItem
import com.github.bumblebee202111.doubean.ui.common.rememberLazyListStatePagingWorkaround
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.getColorFromTheme

@Composable
fun GroupTabScreen(
    groupId: String,
    tabId: String?,
    group: GroupDetail?,
    onTopicClick: (topicId: String) -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
    viewModel: GroupTabViewModel = hiltViewModel<GroupTabViewModel, GroupTabViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(groupId, tabId)
        },
        key = groupId + tabId
    ),
) {
    val topicPagingItems = viewModel.topicsPagingData.collectAsLazyPagingItems()
    val shouldDisplayFavoritedTab = viewModel.shouldDisplayFavoritedTab
    val shouldDisplayUnfavoritedTab = viewModel.shouldDisplayUnfavoritedTab
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()

    GroupTabScreen(
        tabId = tabId,
        topicPagingItems = topicPagingItems,
        shouldDisplayFavoritedGroup = shouldDisplayFavoritedTab,
        shouldDisplayUnfavoritedTab = shouldDisplayUnfavoritedTab,
        sortBy = sortBy,
        group = group,
        clearFavoritedTabState = viewModel::clearFavoritedTabState,
        clearUnfavoritedTabState = viewModel::clearUnfavoritedTabState,
        updateSortBy = viewModel::updateSortBy,
        removeFavorite = viewModel::removeFavorite,
        addFavorite = viewModel::addFavorite,
        saveNotificationsPreference = viewModel::saveNotificationsPreference,
        onTopicClick = onTopicClick,
        onShowSnackbar = onShowSnackbar
    )
}

@Composable
fun GroupTabScreen(
    tabId: String?,
    topicPagingItems: LazyPagingItems<TopicItem>,
    shouldDisplayFavoritedGroup: Boolean,
    shouldDisplayUnfavoritedTab: Boolean,
    sortBy: TopicSortBy?,
    group: GroupDetail?,
    clearFavoritedTabState: () -> Unit,
    clearUnfavoritedTabState: () -> Unit,
    updateSortBy: (topicSortBy: TopicSortBy) -> Unit,
    removeFavorite: () -> Unit,
    addFavorite: () -> Unit,
    saveNotificationsPreference: (enableNotifications: Boolean, allowNotificationUpdates: Boolean, sortRecommendedTopicsBy: TopicSortBy, numberOfTopicsLimitEachFeedFetch: Int) -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
) {
    val context = LocalContext.current

    var openAlertDialog by remember { mutableStateOf(false) }

    val favoritedTabMessage = stringResource(id = R.string.favorited_tab)

    LaunchedEffect(key1 = shouldDisplayFavoritedGroup) {
        if (shouldDisplayFavoritedGroup) {
            onShowSnackbar(favoritedTabMessage)
            clearFavoritedTabState()
        }
    }

    val unfavoritedTabMessage = stringResource(id = R.string.unfavorited_tab)

    LaunchedEffect(key1 = shouldDisplayUnfavoritedTab) {
        if (shouldDisplayUnfavoritedTab) {
            onShowSnackbar(unfavoritedTabMessage)
            clearUnfavoritedTabState()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(rememberNestedScrollInteropConnection()),
        state = topicPagingItems.rememberLazyListStatePagingWorkaround()

    ) {

        tabActionsItem(
            tabId = tabId,
            group = group,
            sortBy = sortBy,
            onOpenAlertDialog = { openAlertDialog = true },
            onSortByClick = updateSortBy,
            removeFavorite = removeFavorite,
            addFavorite = addFavorite
        )

        topicItems(topicPagingItems = topicPagingItems, group = group, onTopicClick = onTopicClick)
    }

    if (openAlertDialog) {
        group?.tabs?.find { it.id == tabId }?.let { tab ->
            val enableNotifications = tab.enableNotifications
            val allowNotificationUpdates = tab.allowDuplicateNotifications
            val sortRecommendedTopicsBy = tab.sortRecommendedTopicsBy
            val numberOfTopicsLimitEachFeedFetch = tab.feedRequestTopicCountLimit
            if (enableNotifications != null && allowNotificationUpdates != null && sortRecommendedTopicsBy != null && numberOfTopicsLimitEachFeedFetch != null) {

                GroupTabNotificationsPreferenceDialog(
                    initialEnableNotifications = enableNotifications,
                    initialAllowNotificationUpdates = allowNotificationUpdates,
                    initialSortRecommendedTopicsBy = sortRecommendedTopicsBy,
                    initialNumberOfTopicsLimitEachFeedFetch = numberOfTopicsLimitEachFeedFetch,
                    onDismissRequest = {
                        openAlertDialog = false
                    }) { enableNotificationsToSave, allowNotificationUpdatesToSave, sortRecommendedTopicsByToSave, numberOfTopicsLimitEachFeedFetchToSave ->
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
    }
}

private fun LazyListScope.tabActionsItem(
    tabId: String?,
    group: GroupDetail?,
    sortBy: TopicSortBy?,
    onOpenAlertDialog: () -> Unit,
    onSortByClick: (topicSortBy: TopicSortBy) -> Unit,
    removeFavorite: () -> Unit,
    addFavorite: () -> Unit,
) {

    item(
        key = "tab_actions", contentType = "tab_actions"
    ) {
        val context = LocalContext.current

        AndroidViewBinding(
            factory = { inflater, parent, attachToParent ->
                ViewGroupTabActionsBinding.inflate(
                    inflater,
                    parent,
                    attachToParent
                ).apply {

                }
            },
            modifier = Modifier.fillMaxWidth(),
            onReset = {}
        ) {

            sortTopicsBySpinner.setContent {
                SortTopicsBySpinner(
                    initialSelectedItem = sortBy,
                    onItemSelected = onSortByClick
                )
            }

            fun setupFavoriteButtonAndMore() {

                if (tabId == null) { 
                    notificationsButton.visibility = View.GONE
                    favoriteButton.visibility = View.GONE
                    more.visibility = View.GONE
                } else { 
                    favoriteButton.setOnClickListener {
                        group?.findTab(tabId)?.let { tab ->
                            if (tab.isFavorite) {
                                removeFavorite()
                            } else {
                                addFavorite()
                            }
                        }
                    }

                    @Suppress("ConstantConditionIf") 
                    if (false) {
                        notificationsButton.setContent {
                            group?.let { group ->
                                group.findTab(tabId)?.let { tab ->
                                    NotificationsButton(
                                        groupColor = group.color?.let {
                                            Color(it)
                                        } ?: LocalContentColor.current,
                                        enableNotifications = tab.enableNotifications ?: false
                                    ) {
                                        onOpenAlertDialog()
                                    }
                                }
                            }
                        }
                    }

                    more.setOnClickListener { v ->
                        val popup = PopupMenu(context, v)
                        popup.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.action_share -> {
                                    val shareText = StringBuilder()
                                    group?.let { group ->
                                        shareText.append(group.name + "|")
                                        group.tabs?.first { it.id == tabId }
                                            ?.let { tab ->
                                                shareText.append(tab.name)
                                            }
                                        group.shareUrl?.let { shareUrl ->
                                            shareText.append(" $shareUrl\r\n")
                                        }

                                        ShareUtil.share(context, shareText)
                                        true
                                    }

                                }
                            }
                            false
                        }

                        popup.inflate(R.menu.menu_group_tab)
                        popup.show()
                    }
                }
            }

            setupFavoriteButtonAndMore()

            group?.let { group ->
                val colorSurface = context.getColorFromTheme(R.attr.colorSurface)
                val groupColor =
                    group.color ?: context.getColorFromTheme(R.attr.colorPrimary)

                group.findTab(tabId)?.let { tab ->
                    with(favoriteButton) {
                        if (tab.isFavorite) {
                            setIconResource(R.drawable.ic_star)
                            setText(R.string.favorited)
                            iconTint = ColorStateList.valueOf(groupColor)
                            setTextColor(groupColor)
                            setBackgroundColor(colorSurface)
                        } else {
                            setIconResource(R.drawable.ic_star)
                            setText(R.string.favorite)
                            iconTint = ColorStateList.valueOf(colorSurface)
                            setTextColor(colorSurface)
                            setBackgroundColor(groupColor)
                        }
                    }

                }
            }
        }
    }
}

private fun LazyListScope.topicItems(
    topicPagingItems: LazyPagingItems<TopicItem>,
    group: GroupDetail?,
    onTopicClick: (topicId: String) -> Unit,
) {
    items(
        count = topicPagingItems.itemCount,
        key = topicPagingItems.itemKey { it.id },
        contentType = topicPagingItems.itemContentType { "topicItem" }
    ) { index ->
        val topic = topicPagingItems[index]
        TopicItem(
            topic = topic,
            group = group?.toItem(),
            displayMode = TopicItemDisplayMode.SHOW_AUTHOR,
            onTopicClick = onTopicClick
        )
        if (index != topicPagingItems.itemCount - 1) {
            HorizontalDivider()
        }
    }
}


@Composable
private fun GroupTabNotificationsPreferenceDialog(
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
    var numberOfTopicsLimitEachFeedFetch by remember {
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
            AndroidViewBinding(factory = DialogContentGroupTabNotificationsPreferenceBinding::inflate) {

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
                sortRecommendedTopicsBySpinner.setContent {
                    SortTopicsBySpinner(
                        initialSelectedItem = sortRecommendedTopicsBy,
                        isEnabled = enableNotifications
                    ) {
                        sortRecommendedTopicsBy = it
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


enum class TopicItemDisplayMode {
    SHOW_AUTHOR, SHOW_GROUP
}