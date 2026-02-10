package com.github.bumblebee202111.doubean.feature.groups.topic

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LocalPinnableContainer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.awaitNotLoading
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.CachedAppResult
import com.github.bumblebee202111.doubean.model.data
import com.github.bumblebee202111.doubean.model.doulists.ItemDouList
import com.github.bumblebee202111.doubean.model.groups.TopicComment
import com.github.bumblebee202111.doubean.model.groups.TopicCommentSortBy
import com.github.bumblebee202111.doubean.model.groups.TopicDetail
import com.github.bumblebee202111.doubean.ui.common.CollectDialogUiState
import com.github.bumblebee202111.doubean.ui.common.CreateDouListDialog
import com.github.bumblebee202111.doubean.ui.common.DouListDialog
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator
import com.github.bumblebee202111.doubean.ui.component.SortByDropDownMenu
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import com.github.bumblebee202111.doubean.util.OpenInUtils
import kotlinx.coroutines.yield

@Composable
fun TopicScreen(
    onBackClick: () -> Unit,
    onWebViewClick: (String) -> Unit,
    onGroupClick: (String, String?) -> Unit,
    onReshareStatusesClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onOpenDeepLinkUrl: (String, Boolean) -> Boolean,
    viewModel: TopicViewModel = hiltViewModel(),
) {
    val topicResult by viewModel.topicResult.collectAsStateWithLifecycle()
    val popularComments: List<TopicComment> by viewModel.popularComments.collectAsStateWithLifecycle()
    val allCommentLazyPagingItems = viewModel.allComments.collectAsLazyPagingItems()
    val shouldShowPhotoList by viewModel.shouldShowPhotoList.collectAsStateWithLifecycle()
    val contentHtml by viewModel.contentHtml.collectAsStateWithLifecycle()
    val commentSortBy by viewModel.commentsSortBy.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val shouldShowSpinner by viewModel.shouldShowSpinner.collectAsStateWithLifecycle()
    val collectDialogUiState by viewModel.collectDialogUiState.collectAsStateWithLifecycle()
    val showCreateDouListDialog by viewModel.showCreateDouListDialog.collectAsStateWithLifecycle()
    val authorOnlyMode by viewModel.authorOnlyMode.collectAsStateWithLifecycle()

    TopicScreen(
        topicResult = topicResult,
        popularComments = popularComments,
        allCommentLazyPagingItems = allCommentLazyPagingItems,
        shouldShowPhotoList = shouldShowPhotoList,
        contentHtml = contentHtml,
        commentSortBy = commentSortBy,
        isLoggedIn = isLoggedIn,
        shouldShowSpinner = shouldShowSpinner,
        collectDialogUiState = collectDialogUiState,
        showCreateDouListDialog = showCreateDouListDialog,
        authorOnlyMode = authorOnlyMode,
        updateCommentSortBy = viewModel::updateCommentsSortBy,
        updateAuthorOnlyMode = viewModel::updateAuthorOnlyMode,
        displayInvalidImageUrl = viewModel::displayInvalidImageUrl,
        onBackClick = onBackClick,
        onWebViewClick = onWebViewClick,
        onGroupClick = onGroupClick,
        onReshareStatusesClick = onReshareStatusesClick,
        onUserClick = onUserClick,
        // TODO
        // https://developer.android.google.cn/develop/ui/compose/touch-input/pointer-input/tap-and-press
        // shared element
        onImageClick = onImageClick,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl,
        onReact = viewModel::react,
        onRefresh = {
            viewModel.refresh()
            allCommentLazyPagingItems.refresh()
        },
        onCollectClick = viewModel::collect,
        onDismissCollectDialog = viewModel::dismissCollectDialog,
        onToggleCollection = viewModel::toggleCollection,
        onCreateDouList = viewModel::showCreateDialog,
        onDismissCreateDialog = viewModel::dismissCreateDialog,
        onCreateAndCollect = viewModel::createAndCollect,
    )
}

@SuppressLint("ClickableViewAccessibility")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(
    topicResult: CachedAppResult<TopicDetail, TopicDetail?>?,
    popularComments: List<TopicComment>,
    allCommentLazyPagingItems: LazyPagingItems<TopicComment>,
    shouldShowPhotoList: Boolean?,
    contentHtml: String?,
    commentSortBy: TopicCommentSortBy,
    isLoggedIn: Boolean,
    shouldShowSpinner: Boolean,
    collectDialogUiState: CollectDialogUiState?,
    showCreateDouListDialog: Boolean,
    authorOnlyMode: Boolean,
    updateCommentSortBy: (TopicCommentSortBy) -> Unit,
    updateAuthorOnlyMode: (Boolean) -> Unit,
    displayInvalidImageUrl: () -> Unit,
    onBackClick: () -> Unit,
    onWebViewClick: (String) -> Unit,
    onGroupClick: (String, String?) -> Unit,
    onReshareStatusesClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onOpenDeepLinkUrl: (String, Boolean) -> Boolean,
    onReact: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    onCollectClick: () -> Unit,
    onDismissCollectDialog: () -> Unit,
    onToggleCollection: (douList: ItemDouList) -> Unit,
    onCreateDouList: () -> Unit,
    onDismissCreateDialog: () -> Unit,
    onCreateAndCollect: (title: String) -> Unit,
) {
    val context = LocalContext.current
    var shouldShowDialog by rememberSaveable { mutableStateOf(false) }

    var scrollToCommentItemIndex: Int? by remember { mutableStateOf(null) }
    val listState = rememberLazyListState()

    val itemCountBeforeComments = 2

    LaunchedEffect(scrollToCommentItemIndex) {
        val targetIndex = scrollToCommentItemIndex ?: return@LaunchedEffect
        val maxCount = when (commentSortBy) {
            TopicCommentSortBy.POPULAR -> popularComments.size
            TopicCommentSortBy.ALL -> allCommentLazyPagingItems.itemCount
        }

        if (maxCount == 0) {
            scrollToCommentItemIndex = null
            return@LaunchedEffect
        }

        val clampedIndex = targetIndex.coerceIn(0, maxCount - 1)

        if (commentSortBy == TopicCommentSortBy.ALL) {
            // Access the item to trigger loading of its page if it's currently a placeholder
            if (allCommentLazyPagingItems.peek(clampedIndex) == null) {
                allCommentLazyPagingItems[clampedIndex]
                // Wait for the load to complete so the real item is available (for accurate height)
                snapshotFlow { allCommentLazyPagingItems.loadState }
                    .awaitNotLoading()
                // Allow one frame for recomposition/measurement of the newly loaded item
                yield()
            }
        }

        // Jump only once to the final calculated position
        listState.scrollToItem(itemCountBeforeComments + clampedIndex)
        scrollToCommentItemIndex = null
    }

    val isRefreshingComments = allCommentLazyPagingItems.loadState.refresh is LoadState.Loading

    Scaffold(
        topBar = {
            val canJump = when (commentSortBy) {
                TopicCommentSortBy.POPULAR -> popularComments.isNotEmpty()
                TopicCommentSortBy.ALL -> allCommentLazyPagingItems.itemCount > 0
            }
            TopicTopAppBar(
                topic = topicResult?.data,
                canJump = canJump,
                onBackClick = onBackClick,
                onJumpToCommentClick = { shouldShowDialog = true },
                onWebViewClick = onWebViewClick,
                onOpenInDoubanClick = { OpenInUtils.openInDouban(context, it) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (topicResult) {
            is CachedAppResult.Loading, null -> {
                FullScreenLoadingIndicator(contentPadding = innerPadding)
            }

            is CachedAppResult.Error -> {
                FullScreenErrorWithRetry(
                    message = topicResult.error.asUiMessage().getString(),
                    onRetryClick = onRefresh,
                    contentPadding = innerPadding
                )
            }

            is CachedAppResult.Success -> {
                val topic = topicResult.data
                if (contentHtml != null) {
                    PullToRefreshBox(
                        isRefreshing = isRefreshingComments,
                        onRefresh = onRefresh,
                        modifier = Modifier
                            .padding(
                                top = innerPadding.calculateTopPadding(),
                                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                            )
                            .fillMaxSize()
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {

                            item(key = "TopicHeader", contentType = "TopicHeader") {
                                val pinnableContainer = LocalPinnableContainer.current
                                DisposableEffect(pinnableContainer) {
                                    val pinnedHandle = pinnableContainer?.pin()
                                    onDispose {
                                        pinnedHandle?.release()
                                    }
                                }

                                TopicHeader(
                                    topic = topic,
                                    shouldShowPhotoList = shouldShowPhotoList,
                                    contentHtml = contentHtml,
                                    isLoggedIn = isLoggedIn,
                                    onImageClick = onImageClick,
                                    onGroupClick = onGroupClick,
                                    onUserClick = onUserClick,
                                    onReshareStatusesClick = onReshareStatusesClick,
                                    onOpenDeepLinkUrl = onOpenDeepLinkUrl,
                                    displayInvalidImageUrl = displayInvalidImageUrl,
                                    onReact = onReact,
                                    onCollectActionInitiated = onCollectClick
                                )
                            }

                            item(
                                key = "CommentActions",
                                contentType = "CommentActions"
                            ) {
                                TopicCommentActions(
                                    shouldShowSpinner = shouldShowSpinner,
                                    commentSortBy = commentSortBy,
                                    updateCommentSortBy = updateCommentSortBy,
                                    authorOnlyMode = authorOnlyMode,
                                    updateAuthorOnlyMode = updateAuthorOnlyMode
                                )
                            }
                            when (commentSortBy) {
                                TopicCommentSortBy.POPULAR -> {
                                    popularComments(
                                        comments = popularComments,
                                        topic = topic,
                                        onUserClick = onUserClick,
                                        onImageClick = onImageClick
                                    )
                                }

                                TopicCommentSortBy.ALL -> {
                                    if (authorOnlyMode && allCommentLazyPagingItems.itemCount == 0 && allCommentLazyPagingItems.loadState.refresh is LoadState.NotLoading) {
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(32.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.empty_author_comments_list),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                    allComments(
                                        comments = allCommentLazyPagingItems,
                                        topic = topic,
                                        onUserClick = onUserClick,
                                        onImageClick = onImageClick
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val commentCount = remember(
        commentSortBy,
        popularComments,
        allCommentLazyPagingItems.itemCount
    ) {
        when (commentSortBy) {
            TopicCommentSortBy.POPULAR -> popularComments.size
            TopicCommentSortBy.ALL -> allCommentLazyPagingItems.itemCount
        }
    }

    if (shouldShowDialog && commentCount > 0) {
        val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
        JumpToCommentOfIndexDialog(
            currentCommentIndex = (firstVisibleItemIndex - itemCountBeforeComments).coerceAtLeast(
                0
            ),
            commentCount = commentCount,
            onDismissRequest = {
                shouldShowDialog = false
            }) { index ->
            scrollToCommentItemIndex = index
        }
    }

    collectDialogUiState?.let { state ->
        DouListDialog(
            uiState = state,
            onDismissRequest = onDismissCollectDialog,
            onDouListClick = onToggleCollection,
            onCreateClick = onCreateDouList
        )
    }

    if (showCreateDouListDialog) {
        CreateDouListDialog(
            onDismissRequest = onDismissCreateDialog,
            onConfirm = onCreateAndCollect
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicCommentActions(
    shouldShowSpinner: Boolean,
    commentSortBy: TopicCommentSortBy,
    updateCommentSortBy: (TopicCommentSortBy) -> Unit,
    authorOnlyMode: Boolean,
    updateAuthorOnlyMode: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (shouldShowSpinner) {
            TopicCommentSortByDropDownMenu(
                commentSortBy = commentSortBy,
                updateCommentSortBy = updateCommentSortBy,
                modifier = Modifier.weight(1f)
            )
        } else {
            Text(
                text = stringResource(id = R.string.all_comments),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )
        }
        FilterChip(
            selected = authorOnlyMode,
            onClick = { updateAuthorOnlyMode(!authorOnlyMode) },
            label = { Text(stringResource(R.string.author_only_mode)) },
            leadingIcon = if (authorOnlyMode) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            }
        )
    }
}


@Composable
fun TopicCommentSortByDropDownMenu(
    commentSortBy: TopicCommentSortBy,
    updateCommentSortBy: (TopicCommentSortBy) -> Unit,
    modifier: Modifier = Modifier,
) {
    SortByDropDownMenu(
        options = TopicCommentSortOption.displayOrder,
        initialSelectedValue = commentSortBy,
        onOptionSelected = updateCommentSortBy,
        optionText = { stringResource(it.textResId) },
        optionToValue = { it.sortBy },
        modifier = modifier
    )
}


private enum class TopicCommentSortOption(
    @StringRes val textResId: Int,
    val sortBy: TopicCommentSortBy,
) {
    TOP(
        textResId = R.string.top_comments,
        sortBy = TopicCommentSortBy.POPULAR
    ),
    ALL(
        textResId = R.string.all_comments,
        sortBy = TopicCommentSortBy.ALL
    );

    companion object {
        val displayOrder = listOf(TOP, ALL)
    }
}
