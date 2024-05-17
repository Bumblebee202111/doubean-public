package com.github.bumblebee202111.doubean.feature.groups.groupDetail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.LayoutGroupDetailBinding
import com.github.bumblebee202111.doubean.feature.groups.groupDetail.GroupNotificationsPreferenceDialogFragment.Companion.DIALOG_GROUP_NOTIFICATIONS_PREFERENCE
import com.github.bumblebee202111.doubean.feature.groups.groupTab.GroupTabScreen
import com.github.bumblebee202111.doubean.feature.groups.groupTab.GroupTabViewModel
import com.github.bumblebee202111.doubean.model.GroupDetail
import com.github.bumblebee202111.doubean.model.GroupTab
import com.github.bumblebee202111.doubean.ui.theme.AppTheme
import com.github.bumblebee202111.doubean.util.OpenInUtil
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.getColorFromTheme
import com.github.bumblebee202111.doubean.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GroupDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = content {
        AppTheme {
            GroupDetailScreen(groupDetailViewModel = viewModel(),
                showNotificationsPrefDialog = { groupId ->
                    GroupNotificationsPreferenceDialogFragment.newInstance(groupId)
                        .show(childFragmentManager, DIALOG_GROUP_NOTIFICATIONS_PREFERENCE)
                },
                showTabNotificationsPrefDialog = { tabId ->

                    






                },
                onBackClick = {
                    findNavController().popBackStack()
                },
                navigateToTopic = {
                    val direction =
                        GroupDetailFragmentDirections.actionGroupDetailToPostDetail(
                            it
                        )
                    findNavController().navigate(direction)
                }
            )
        }
    }

}

@Composable
fun GroupDetailScreen(
    groupDetailViewModel: GroupDetailViewModel,
    showNotificationsPrefDialog: (groupId: String) -> Unit,
    showTabNotificationsPrefDialog: (tabId: String) -> Unit,
    onBackClick: () -> Unit,
    navigateToTopic: (topicId: String) -> Unit,
) {
    val taggedTabs by groupDetailViewModel.tabs.collectAsStateWithLifecycle()
    val group by groupDetailViewModel.group.collectAsStateWithLifecycle()
    val initialTabId = groupDetailViewModel.initialTabId
    val groupId = groupDetailViewModel.groupId

    val context = LocalContext.current
    Column {
        Spacer(
            Modifier.windowInsetsTopHeight(
                WindowInsets.statusBars
            )
        )
        GroupDetailCoordinator(
            groupId = groupId,
            group = group,
            initialTabId = initialTabId,
            taggedTabs = taggedTabs,
            addFavorite = groupDetailViewModel::addFavorite,
            removeFavorite = groupDetailViewModel::removeFavorite,
            showNotificationsPrefDialog = { showNotificationsPrefDialog(groupId) },
            showTabNotificationsPrefDialog = showTabNotificationsPrefDialog,
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
            navigateToTopic = navigateToTopic,
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupDetailCoordinator(
    groupId: String,
    group: GroupDetail?,
    initialTabId: String?,
    taggedTabs: List<GroupTab>?,
    addFavorite: () -> Unit,
    removeFavorite: () -> Unit,
    showNotificationsPrefDialog: () -> Unit,
    showTabNotificationsPrefDialog: (tabId: String) -> Unit,
    onBackClick: () -> Unit,
    onShareGroup: (group: GroupDetail) -> Unit,
    viewInDouban: (uriString: String) -> Unit,
    viewInBrowser: (urlString: String) -> Unit,
    navigateToTopic: (topicId: String) -> Unit,
) {

    taggedTabs?.let {
        val pagerState = rememberPagerState(
            initialPage = taggedTabs.indexOfFirst { it.id == initialTabId } + 1,
            pageCount = { taggedTabs.size + 1 }
        )
        AndroidViewBinding(factory = LayoutGroupDetailBinding::inflate,
            onReset = {}) {
            val context = root.context

            favoriteButton.setOnClickListener { view ->
                group?.isFavorited?.let { oldIsFollowed ->
                    if (oldIsFollowed) {
                        removeFavorite()
                        view?.showSnackbar(
                            R.string.unfavorited_group,
                            Snackbar.LENGTH_LONG,
                        )
                    } else {
                        addFavorite()
                        view?.showSnackbar(
                            R.string.favorited_group,
                            Snackbar.LENGTH_LONG,
                            R.string.edit_favorite_preferences
                        ) { showNotificationsPrefDialog() }
                    }
                }
            }

            notificationButton.setOnClickListener { showNotificationsPrefDialog() }

            toolbarLayout.setCollapsedTitleTextColor(context.getColor(R.color.doubean_white))
            appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                
                val shouldShowToolbar = verticalOffset + appBarLayout.totalScrollRange == 0
                appbar.isActivated = shouldShowToolbar
            }
            toolbar.setOnMenuItemClickListener { item ->
                return@setOnMenuItemClickListener group?.let { group ->
                    when (item.itemId) {
                        R.id.action_share -> {
                            onShareGroup(group)
                            true
                        }

                        R.id.action_view_in_douban -> {
                            viewInDouban(group.uri)
                            true
                        }

                        R.id.action_view_in_browser -> {
                            viewInBrowser(group.shareUrl)
                            true
                        }

                        else -> {
                            false
                        }
                    }
                } ?: false
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
                    navigateToTopic = navigateToTopic,
                    showTabNotificationsPrefDialog = showTabNotificationsPrefDialog
                )
            }

            this@AndroidViewBinding.group = group
            group?.let { group ->
                group.color?.let { color ->
                    mask.setBackgroundColor(color)
                    toolbarLayout.setContentScrimColor(color)
                    toolbarLayout.setStatusBarScrimColor(color)
                }

                val colorSurface = context.getColorFromTheme(R.attr.colorSurface)
                val groupColor =
                    group.color ?: context.getColorFromTheme(R.attr.colorPrimary)

                with(favoriteButton) {
                    if (group.isFavorited) {
                        setIconResource(R.drawable.ic_remove)
                        setText(R.string.unfavorite)
                        iconTint = ColorStateList.valueOf(groupColor)
                        setTextColor(groupColor)
                        setBackgroundColor(colorSurface)
                    } else {
                        setIconResource(R.drawable.ic_add)
                        setText(R.string.favorite)
                        iconTint = ColorStateList.valueOf(colorSurface)
                        setTextColor(colorSurface)
                        setBackgroundColor(groupColor)
                    }
                }

                with(notificationButton) {
                    when (group.enableNotifications) {
                        true -> {
                            setIconResource(R.drawable.ic_notifications)
                            iconTint = ColorStateList.valueOf(groupColor)
                            setBackgroundColor(colorSurface)
                        }

                        false -> {
                            setIconResource(R.drawable.ic_notification_add)
                            iconTint = ColorStateList.valueOf(colorSurface)
                            setBackgroundColor(groupColor)
                        }

                        else -> {
                        }
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupPager(
    pagerState: PagerState,
    taggedTabs: List<GroupTab>,
    groupId: String,
    group: GroupDetail?,
    navigateToTopic: (topicId: String) -> Unit,
    showTabNotificationsPrefDialog: (tabId: String) -> Unit,
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
            groupTabViewModel = hiltViewModel<GroupTabViewModel, GroupTabViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(groupId, tabId)
                },
                key = groupId + tabId
            ),
            group = group,
            navigateToTopic = navigateToTopic,
            showTabNotificationsPreferenceDialog = showTabNotificationsPrefDialog
        )

    }
}