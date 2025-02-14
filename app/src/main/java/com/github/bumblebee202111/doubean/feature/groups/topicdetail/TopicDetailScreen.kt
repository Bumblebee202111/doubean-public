@file:Suppress("DEPRECATION")

package com.github.bumblebee202111.doubean.feature.groups.topicdetail

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.webkit.URLUtil
import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.awaitNotLoading
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.TopicComment
import com.github.bumblebee202111.doubean.model.TopicCommentSortBy
import com.github.bumblebee202111.doubean.model.TopicDetail
import com.github.bumblebee202111.doubean.ui.SmallGroupAvatar
import com.github.bumblebee202111.doubean.ui.UserProfileImage
import com.github.bumblebee202111.doubean.ui.common.DoubeanWebView
import com.github.bumblebee202111.doubean.ui.common.TopicWebViewClient
import com.github.bumblebee202111.doubean.ui.component.DateTimeText
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.util.OpenInUtils
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.TOPIC_CSS_FILENAME
import com.github.bumblebee202111.doubean.util.fullDateTimeString
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberSaveableWebViewState
import com.google.accompanist.web.rememberWebViewNavigator

@Composable
fun TopicDetailScreen(
    onBackClick: () -> Unit,
    onWebViewClick: (url: String) -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onReshareStatusesClick: (topicId: String) -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Unit,
    viewModel: TopicDetailViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String) -> Unit,
) {
    val topic by viewModel.topic.collectAsStateWithLifecycle()
    val popularComments: List<TopicComment> by viewModel.popularComments.collectAsStateWithLifecycle()
    val allCommentLazyPagingItems = viewModel.allComments.collectAsLazyPagingItems()
    val contentHtml by viewModel.contentHtml.collectAsStateWithLifecycle()
    val commentSortBy by viewModel.commentsSortBy.collectAsStateWithLifecycle()
    val groupColorInt = topic?.group?.color
    val shouldShowSpinner by viewModel.shouldShowSpinner.collectAsStateWithLifecycle()
    val shouldDisplayInvalidImageUrl = viewModel.shouldDisplayInvalidImageUrl

    TopicDetailScreen(
        topic = topic,
        popularCommentsLazyPagingItems = popularComments,
        allCommentLazyPagingItems = allCommentLazyPagingItems,
        contentHtml = contentHtml,
        commentSortBy = commentSortBy,
        groupColorInt = groupColorInt,
        shouldShowSpinner = shouldShowSpinner,
        shouldDisplayInvalidImageUrl = shouldDisplayInvalidImageUrl,
        updateCommentSortBy = viewModel::updateCommentsSortBy,
        displayInvalidImageUrl = viewModel::displayInvalidImageUrl,
        clearInvalidImageUrlState = viewModel::clearInvalidImageUrlState,
        onBackClick = onBackClick,
        onWebViewClick = onWebViewClick,
        onGroupClick = onGroupClick,
        onReshareStatusesClick = onReshareStatusesClick,
        
        
        
        onImageClick = onImageClick,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl,
        onShowSnackbar = onShowSnackbar
    )
}

@SuppressLint("ClickableViewAccessibility")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailScreen(
    topic: TopicDetail?,
    popularCommentsLazyPagingItems: List<TopicComment>,
    allCommentLazyPagingItems: LazyPagingItems<TopicComment>,
    contentHtml: String?,
    commentSortBy: TopicCommentSortBy,
    groupColorInt: Int?,
    shouldShowSpinner: Boolean,
    shouldDisplayInvalidImageUrl: Boolean,
    updateCommentSortBy: (TopicCommentSortBy) -> Unit,
    displayInvalidImageUrl: () -> Unit,
    clearInvalidImageUrlState: () -> Unit,
    onBackClick: () -> Unit,
    onWebViewClick: (url: String) -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onReshareStatusesClick: (topicId: String) -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
) {

    val context = LocalContext.current
    var shouldShowDialog by remember {
        mutableStateOf(false)
    }
    var scrollToCommentItemIndex: Int? by remember {
        mutableStateOf(null)
    }
    val listState = rememberLazyListState()

    val itemCountBeforeComments = 2

    scrollToCommentItemIndex?.let { index ->
        LaunchedEffect(index) {
            listState.scrollToItem(itemCountBeforeComments + index)
            snapshotFlow {
                allCommentLazyPagingItems.loadState
            }.awaitNotLoading()
            listState.scrollToItem(itemCountBeforeComments + index)
            scrollToCommentItemIndex = null
        }
    }

    LaunchedEffect(shouldDisplayInvalidImageUrl) {
        if (shouldDisplayInvalidImageUrl) {
            onShowSnackbar("Invalid Image Url")
            clearInvalidImageUrlState()
        }
    }

    Scaffold(
        topBar = {
            var appBarMenuExpanded by remember { mutableStateOf(false) }
            var viewInMenuExpanded by remember { mutableStateOf(false) }
            val groupColor = groupColorInt?.let(::Color)
            DoubeanTopAppBar(
                title = {
                    topic?.title?.let {
                        Text(
                            text = it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        topic?.let { shareTopic(context, it) }

                    }) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = null
                        )
                    }

                    IconButton(onClick = { appBarMenuExpanded = !appBarMenuExpanded }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = appBarMenuExpanded,
                        onDismissRequest = { appBarMenuExpanded = false }) {
                        if (topic != null) {
                            DropdownMenuItem(text = {
                                Text(text = "Jump to comment")
                            },
                                onClick = {
                                    appBarMenuExpanded = false
                                    shouldShowDialog = true
                                })

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
                                appBarMenuExpanded = false
                                viewInMenuExpanded = true
                            })
                    }

                    DropdownMenu(
                        expanded = viewInMenuExpanded,
                        onDismissRequest = { viewInMenuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.view_in_web)) },
                            onClick = { topic?.url?.let(onWebViewClick) })
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.view_in_douban)) },
                            onClick = {
                                topic?.uri?.let {
                                    OpenInUtils.openInDouban(context, it)
                                }
                            })

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().run {
                    groupColor?.let {
                        copy(containerColor = it)
                    } ?: this
                })
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {

            topic?.let { topic ->
                item(key = "TopicDetailHeader", contentType = "TopicDetailHeader") {
                    TopicDetailHeader(
                        topic = topic,
                        contentHtml = contentHtml,
                        displayInvalidImageUrl = displayInvalidImageUrl,
                        onImageClick = onImageClick,
                        onGroupClick = onGroupClick,
                        onReshareStatusesClick = onReshareStatusesClick,
                        onOpenDeepLinkUrl = onOpenDeepLinkUrl,
                    )
                }
            }

            if (shouldShowSpinner) {
                item(key = "TopicCommentSortBy", contentType = "TopicCommentSortBy") {
                    TopicCommentSortBy(
                        commentSortBy = commentSortBy,
                        updateCommentSortBy = updateCommentSortBy
                    )
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

            }
            if (topic != null && groupColorInt != null) {
                when (commentSortBy) {
                    TopicCommentSortBy.TOP -> {
                        items(
                            count = popularCommentsLazyPagingItems.size,
                            key = { popularCommentsLazyPagingItems[it].id },
                            contentType = { "TopicComment" }) { index ->
                            TopicComment(
                                comment = popularCommentsLazyPagingItems[index],
                                groupColorInt = groupColorInt,
                                topic = topic,
                                onImageClick = onImageClick
                            )
                            if (index < popularCommentsLazyPagingItems.size - 1)
                                HorizontalDivider(thickness = 1.dp)
                        }
                    }

                    TopicCommentSortBy.ALL -> {
                        items(
                            count = allCommentLazyPagingItems.itemCount,
                            key = allCommentLazyPagingItems.itemKey { it.id },
                            contentType = allCommentLazyPagingItems.itemContentType { "TopicComment" }) { index ->
                            TopicComment(
                                comment = allCommentLazyPagingItems[index],
                                groupColorInt = groupColorInt,
                                topic = topic,
                                onImageClick = onImageClick
                            )
                            if (index < allCommentLazyPagingItems.itemCount - 1)
                                HorizontalDivider(thickness = 1.dp)
                        }
                    }
                }
            }


        }
    }
    (if (commentSortBy == TopicCommentSortBy.TOP) popularCommentsLazyPagingItems.size else topic?.commentCount)?.let {
        if (shouldShowDialog) {

            JumpToCommentOfIndexDialog(
                currentCommentIndex = (listState.firstVisibleItemIndex - itemCountBeforeComments).coerceAtLeast(
                    0
                ),
                commentCount = it,
                onDismissRequest = {
                    shouldShowDialog = false
                }) { index ->
                scrollToCommentItemIndex = index
            }
        }
    }

}

@Composable
fun JumpToCommentOfIndexDialog(
    currentCommentIndex: Int, commentCount: Int, onDismissRequest: () -> Unit,
    jumpToCommentOfIndex: (newCommentIndex: Int) -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            JumpToCommentOfIndexSlider(
                currentCommentIndex = currentCommentIndex,
                commentCount = commentCount,
                onTargetCommentIndexConfirmed = {
                    onDismissRequest()
                    jumpToCommentOfIndex(it)
                }
            )
        }
    }
}

@Composable
fun JumpToCommentOfIndexSlider(
    currentCommentIndex: Int,
    commentCount: Int,
    onTargetCommentIndexConfirmed: (newCommentIndex: Int) -> Unit,
) {
    var sliderPosition by remember { mutableIntStateOf(currentCommentIndex) }
    val steps = commentCount - 1
    Column(modifier = Modifier.padding(16.dp)) {
        fun Int.toDisplayPosition() = (this + 1).toString()

        Slider(
            value = sliderPosition.toFloat(),
            onValueChange = { sliderPosition = it.toInt() },
            steps = steps - 1,
            valueRange = 0f..steps.toFloat(),
            onValueChangeFinished = { onTargetCommentIndexConfirmed(sliderPosition) },
        )
        Text(
            text = "${sliderPosition.toDisplayPosition()}/$commentCount",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }

}

@Preview
@Composable
fun JumpToCommentSliderPreview() {
    JumpToCommentOfIndexSlider(100, 1000) {}
}

@SuppressLint("ClickableViewAccessibility")
@Composable
fun TopicDetailHeader(
    topic: TopicDetail,
    contentHtml: String?,
    onImageClick: (url: String) -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onReshareStatusesClick: (topicId: String) -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Unit,
    displayInvalidImageUrl: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        topic.group?.let { group ->
            Row(Modifier.clickable {
                onGroupClick(
                    group.id, null
                )
            }, verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(24.dp))
                SmallGroupAvatar(avatarUrl = group.avatarUrl)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = group.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(8.dp))
        }
        Row {
            UserProfileImage(
                url = topic.author.avatarUrl,
                size = dimensionResource(id = R.dimen.icon_size_large)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = topic.author.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    topic.createTime?.let {
                        DateTimeText(text = it.fullDateTimeString())
                    }
                    Spacer(Modifier.width(8.dp))
                    topic.ipLocation?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(text = topic.title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        topic.tag?.let { tag ->
            AssistChip(
                onClick = {
                    topic.group?.let { group ->
                        onGroupClick(
                            group.id,
                            tag.id
                        )
                    }
                },
                label = {
                    Text(tag.name)
                }
            )
        }
        Spacer(Modifier.height(8.dp))
        contentHtml?.let {

            val webViewState = rememberSaveableWebViewState()
            val navigator = rememberWebViewNavigator()

            LaunchedEffect(navigator) {
                val bundle = webViewState.viewState
                if (bundle == null) {
                    
                    navigator.loadHtml(it)
                }
            }
            WebView(state = webViewState,
                navigator = navigator,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.99F),
                captureBackPresses = false,
                client = remember {
                    object : TopicWebViewClient(listOf(TOPIC_CSS_FILENAME)) {
                        @Deprecated("Deprecated in Java")
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            url: String?,
                        ): Boolean {
                            if (url == null) return false

                            try {
                                onOpenDeepLinkUrl(url)
                            } catch (e: IllegalArgumentException) {
                                Log.i("doubean", "shouldOverrideUrlLoading: $e")
                                OpenInUtils.viewInActivity(context, url)
                            }
                            return true
                        }

                    }
                },
                factory = { context ->
                    DoubeanWebView(context).apply {
                        setPadding(0, 0, 0, 0)
                        settings.apply {
                            setNeedInitialFocus(false)
                            
                            useWideViewPort = true
                        }

                        setOnTouchListener { _, event ->
                            if (event.action == MotionEvent.ACTION_UP
                            ) {
                                val webViewHitTestResult = getHitTestResult()
                                if (webViewHitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
                                    webViewHitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
                                ) {
                                    val imageUrl = webViewHitTestResult.extra
                                    if (URLUtil.isValidUrl(imageUrl)) {
                                        val largeImageUrl =
                                            topic.images!!.first { it.normal.url == imageUrl }.large.url
                                        onImageClick(largeImageUrl)
                                    } else {
                                        displayInvalidImageUrl()
                                    }
                                }
                            }
                            return@setOnTouchListener false
                        }
                    }
                })

        }
        Row(
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.margin_small),
                    end = dimensionResource(id = R.dimen.margin_normal),
                    top = dimensionResource(id = R.dimen.margin_normal)
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            topic.commentCount?.let {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.comments,
                        count = it,
                        it
                    )
                )
            }
            topic.resharesCount?.let {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.reshares,
                        count = it,
                        it
                    ),
                    modifier = Modifier.run {
                        if (it != 0) {
                            clickable {
                                onReshareStatusesClick(topic.id)
                            }
                        } else {
                            this
                        }
                    }
                )
            }
            topic.likeCount?.let {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.likes,
                        count = it,
                        it
                    )
                )
            }
            topic.saveCount?.let {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.saves,
                        count = it,
                        it
                    )
                )
            }
        }
    }
}

@Composable
fun TopicCommentSortBy(
    commentSortBy: TopicCommentSortBy,
    updateCommentSortBy: (TopicCommentSortBy) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val sortCommentsByLabels =
        stringArrayResource(id = R.array.sort_comments_by_array)

    val indexToSortBy = mapOf(
        0 to TopicCommentSortBy.TOP,
        1 to TopicCommentSortBy.ALL
    )

    val sortByToIndex = mapOf(
        TopicCommentSortBy.TOP to 0,
        TopicCommentSortBy.ALL to 1
    )

    Box(
        Modifier.padding(
            horizontal = dimensionResource(id = R.dimen.margin_normal),
            vertical = dimensionResource(id = R.dimen.margin_small)
        ),
    ) {
        Button(onClick = { expanded = !expanded }) {
            Text(sortCommentsByLabels[sortByToIndex[commentSortBy]!!])
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            sortCommentsByLabels.forEachIndexed { index, label ->
                DropdownMenuItem(
                    text = { Text(text = label) },
                    onClick = {
                        expanded = false
                        updateCommentSortBy(
                            indexToSortBy[index]!!
                        )
                    })
            }
        }
    }
}

private fun shareTopic(context: Context, topic: TopicDetail) {
    val shareText = StringBuilder()
    topic.group?.name.let { groupName ->
        shareText.append(groupName)
    }
    topic.tag?.let { tag -> shareText.append("|" + tag.name) }
    shareText.append("@${topic.author.name}： ${topic.title}${topic.url}${topic.content}")
    ShareUtil.share(context = context, shareText = shareText)
}
