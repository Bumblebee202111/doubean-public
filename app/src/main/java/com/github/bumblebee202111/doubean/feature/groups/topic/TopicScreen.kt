@file:Suppress("DEPRECATION")

package com.github.bumblebee202111.doubean.feature.groups.topic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.webkit.URLUtil
import android.webkit.WebView
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AssistChip
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LocalPinnableContainer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
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
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.SmallGroupAvatar
import com.github.bumblebee202111.doubean.feature.groups.shared.groupTopAppBarColor
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.fangorns.ReactionType
import com.github.bumblebee202111.doubean.model.groups.TopicComment
import com.github.bumblebee202111.doubean.model.groups.TopicCommentSortBy
import com.github.bumblebee202111.doubean.model.groups.TopicDetail
import com.github.bumblebee202111.doubean.ui.component.DateTimeText
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.DoubeanWebView
import com.github.bumblebee202111.doubean.ui.component.DoubeanWebViewClient
import com.github.bumblebee202111.doubean.ui.component.SortByDropDownMenu
import com.github.bumblebee202111.doubean.ui.component.UserProfileImage
import com.github.bumblebee202111.doubean.util.OpenInUtils
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.TOPIC_CSS_FILENAME
import com.github.bumblebee202111.doubean.util.fullDateTimeString
import com.github.bumblebee202111.doubean.util.toColorOrPrimary
import com.github.bumblebee202111.doubean.util.uiMessage
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import kotlinx.coroutines.delay

@Composable
fun TopicScreen(
    onBackClick: () -> Unit,
    onWebViewClick: (url: String) -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onReshareStatusesClick: (topicId: String) -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Unit,
    viewModel: TopicViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String) -> Unit,
) {
    val topic by viewModel.topic.collectAsStateWithLifecycle()
    val popularComments: List<TopicComment> by viewModel.popularComments.collectAsStateWithLifecycle()
    val allCommentLazyPagingItems = viewModel.allComments.collectAsLazyPagingItems()
    val shouldShowPhotoList by viewModel.shouldShowPhotoList.collectAsStateWithLifecycle()
    val contentHtml by viewModel.contentHtml.collectAsStateWithLifecycle()
    val commentSortBy by viewModel.commentsSortBy.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val groupColor = topic?.group?.color
    val uiError by viewModel.uiError.collectAsStateWithLifecycle()
    val shouldShowSpinner by viewModel.shouldShowSpinner.collectAsStateWithLifecycle()
    val shouldDisplayInvalidImageUrl = viewModel.shouldDisplayInvalidImageUrl

    TopicScreen(
        topic = topic,
        popularCommentsLazyPagingItems = popularComments,
        allCommentLazyPagingItems = allCommentLazyPagingItems,
        shouldShowPhotoList = shouldShowPhotoList,
        contentHtml = contentHtml,
        commentSortBy = commentSortBy,
        isLoggedIn = isLoggedIn,
        groupColorString = groupColor,
        shouldShowSpinner = shouldShowSpinner,
        shouldDisplayInvalidImageUrl = shouldDisplayInvalidImageUrl,
        uiError = uiError,
        updateCommentSortBy = viewModel::updateCommentsSortBy,
        displayInvalidImageUrl = viewModel::displayInvalidImageUrl,
        clearInvalidImageUrlState = viewModel::clearInvalidImageUrlState,
        onBackClick = onBackClick,
        onWebViewClick = onWebViewClick,
        onGroupClick = onGroupClick,
        onReshareStatusesClick = onReshareStatusesClick,
        // TODO
        // https://developer.android.google.cn/develop/ui/compose/touch-input/pointer-input/tap-and-press
        // shared element
        onImageClick = onImageClick,
        onClearUiError = viewModel::clearUiError,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl,
        onShowSnackbar = onShowSnackbar,
        onReact = viewModel::react
    )
}

@SuppressLint("ClickableViewAccessibility")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(
    topic: TopicDetail?,
    popularCommentsLazyPagingItems: List<TopicComment>,
    allCommentLazyPagingItems: LazyPagingItems<TopicComment>,
    shouldShowPhotoList: Boolean?,
    contentHtml: String?,
    commentSortBy: TopicCommentSortBy,
    isLoggedIn: Boolean,
    groupColorString: String?,
    shouldShowSpinner: Boolean,
    shouldDisplayInvalidImageUrl: Boolean,
    uiError: AppError?,
    updateCommentSortBy: (TopicCommentSortBy) -> Unit,
    displayInvalidImageUrl: () -> Unit,
    clearInvalidImageUrlState: () -> Unit,
    onBackClick: () -> Unit,
    onWebViewClick: (url: String) -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onReshareStatusesClick: (topicId: String) -> Unit,
    onImageClick: (url: String) -> Unit,
    onClearUiError: () -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
    onReact: (Boolean) -> Unit,
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

    val uiMessage = uiError?.uiMessage
    LaunchedEffect(uiError) {
        if (uiMessage != null) {
            onShowSnackbar(uiMessage)
            onClearUiError()
        }
    }

    Scaffold(
        topBar = {
            var appBarMenuExpanded by remember { mutableStateOf(false) }
            var viewInMenuExpanded by remember { mutableStateOf(false) }
            val groupColor = groupColorString.toColorOrPrimary()
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
                            DropdownMenuItem(
                                text = {
                                    Text(text = "Jump to comment")
                                },
                                onClick = {
                                    appBarMenuExpanded = false
                                    shouldShowDialog = true
                                })

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
                colors = groupTopAppBarColor(groupColor)
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        if (contentHtml != null) {
            LazyColumn(
                contentPadding = paddingValues,
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {

                topic?.let { topic ->
                    item(key = "TopicDetailHeader", contentType = "TopicDetailHeader") {
                        LocalPinnableContainer.current?.pin()
                        TopicHeader(
                            topic = topic,
                            shouldShowPhotoList = shouldShowPhotoList,
                            contentHtml = contentHtml,
                            isLoggedIn = isLoggedIn,
                            onImageClick = onImageClick,
                            onGroupClick = onGroupClick,
                            onReshareStatusesClick = onReshareStatusesClick,
                            onOpenDeepLinkUrl = onOpenDeepLinkUrl,
                            displayInvalidImageUrl = displayInvalidImageUrl,
                            onReact = onReact,
                        )
                    }
                }

                if (shouldShowSpinner) {
                    item(key = "TopicCommentSortBy", contentType = "TopicCommentSortBy") {
                        TopicCommentSortByDropDownMenu(
                            commentSortBy = commentSortBy,
                            updateCommentSortBy = updateCommentSortBy,
                            modifier = Modifier.padding(
                                horizontal = dimensionResource(id = R.dimen.margin_normal),
                                vertical = dimensionResource(id = R.dimen.margin_small)
                            )
                        )
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                }
                if (topic != null) {
                    when (commentSortBy) {
                        TopicCommentSortBy.TOP -> {
                            items(
                                count = popularCommentsLazyPagingItems.size,
                                key = { popularCommentsLazyPagingItems[it].id },
                                contentType = { "TopicComment" }) { index ->
                                TopicComment(
                                    comment = popularCommentsLazyPagingItems[index],
                                    groupColor = groupColorString,
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
                                allCommentLazyPagingItems[index]?.let {
                                    TopicComment(
                                        comment = it,
                                        groupColor = groupColorString,
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

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("ClickableViewAccessibility")
@Composable
fun TopicHeader(
    topic: TopicDetail,
    shouldShowPhotoList: Boolean?,
    contentHtml: String?,
    isLoggedIn: Boolean,
    onImageClick: (url: String) -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onReshareStatusesClick: (topicId: String) -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Unit,
    displayInvalidImageUrl: () -> Unit,
    onReact: (Boolean) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
            Spacer(Modifier.height(12.dp))
            topic.group?.let { group ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .clickable { onGroupClick(group.id, null) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallGroupAvatar(avatarUrl = group.avatar)
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
            Row(Modifier.padding(horizontal = 16.dp)) {
                UserProfileImage(
                    url = topic.author.avatar,
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
                        var showCreateTime by remember { mutableStateOf(true) }
                        val createTime = topic.createTime
                        val editTime = topic.editTime
                        when {
                            editTime != null -> {
                                LaunchedEffect(Unit) {
                                    while (true) {
                                        delay(3000)
                                        showCreateTime = !showCreateTime
                                    }
                                }

                                AnimatedContent(
                                    targetState = showCreateTime,
                                    transitionSpec = {
                                        fadeIn() with fadeOut() using SizeTransform(clip = false)
                                    },
                                    label = "timeAnimation"
                                ) { target ->
                                    DateTimeText(
                                        text = if (target) createTime.fullDateTimeString() else stringResource(
                                            R.string.content_edited, editTime.fullDateTimeString()
                                        )
                                    )
                                }
                            }

                            else -> {
                                DateTimeText(
                                    text = createTime.fullDateTimeString()
                                )
                            }

                        }
                        topic.ipLocation?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            if (shouldShowPhotoList == true) {
                topic.images?.let { images ->
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = images, key = { it.normal.url }) { image ->
                            AsyncImage(
                                model = image.normal.url,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(320.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onImageClick(image.large.url)
                                    },
                                contentScale = ContentScale.FillHeight
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = topic.title,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            topic.tag?.let { tag ->
                AssistChip(
                    onClick = { topic.group?.let { group -> onGroupClick(group.id, tag.id) } },
                    label = { Text(tag.name) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            contentHtml?.let {
                ContentWebView(
                    topic = topic,
                    html = it,
                    onImageClick = onImageClick,
                    displayInvalidImageUrl = displayInvalidImageUrl,
                    onOpenDeepLinkUrl = onOpenDeepLinkUrl
                )
            }
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.margin_normal),
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                topic.commentCount?.let { count ->
                    CountItem(
                        count = count,
                        labelRes = R.plurals.comments
                    )
                }
                topic.resharesCount?.let { count ->
                    CountItem(
                        count = count,
                        labelRes = R.plurals.reshares,
                        onClick = if (count != 0) {
                            { onReshareStatusesClick(topic.id) }
                        } else null
                    )
                }
                topic.likeCount?.let { count ->
                    CountItem(
                        count = count,
                        labelRes = R.plurals.likes
                    )
                }
                topic.collectionsCount?.let { count ->
                    CountItem(
                        count = count,
                        labelRes = R.plurals.saves
                    )
                }
            }
            TopicSocialActions(topic = topic, isLoggedIn = isLoggedIn, onReact = onReact)
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
@Composable
private fun ContentWebView(
    topic: TopicDetail,
    html: String,
    onImageClick: (String) -> Unit,
    displayInvalidImageUrl: () -> Unit,
    onOpenDeepLinkUrl: (String) -> Unit,
) {
    val context = LocalContext.current
    val webViewState = rememberWebViewStateWithHTMLData(html)
    // No need to add Spacer-s around it since its content has big vertical margins
    DoubeanWebView(
        state = webViewState,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .alpha(0.99F),
        captureBackPresses = false,
        onCreated = {
            it.apply {
                setPadding(0, 0, 0, 0)
                setBackgroundColor(Color.TRANSPARENT)
                settings.apply {
                    setNeedInitialFocus(false)
                    //webSettings.userAgentString = DOUBAN_USER_AGENT_STRING
                    useWideViewPort = true
                }

                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP
                    ) {
                        val webViewHitTestResult = getHitTestResult()
                        if (webViewHitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
                            webViewHitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
                        ) {
                            val imageUrl = webViewHitTestResult.extra!!
                            if (URLUtil.isValidUrl(imageUrl)) {
                                val largeImageUrl =
                                    topic.images?.firstOrNull { image -> image.normal.url == imageUrl }?.large?.url
                                        ?: return@setOnTouchListener false
                                onImageClick(largeImageUrl)
                            } else {
                                displayInvalidImageUrl()
                            }
                        }
                    }
                    return@setOnTouchListener false
                }
            }
        },
        client = remember {
            object : DoubeanWebViewClient(listOf(TOPIC_CSS_FILENAME)) {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    url: String?,
                ): Boolean {
                    if (url == null) return false
                    try {
                        onOpenDeepLinkUrl(url)
                    } catch (e: IllegalArgumentException) {
                        Log.i("ContentWebView", "shouldOverrideUrlLoading: $e")
                        OpenInUtils.viewInActivity(context, url)
                    }
                    return true
                }
            }
        },
    )
}

@Composable
private fun CountItem(
    count: Int,
    @PluralsRes labelRes: Int,
    onClick: (() -> Unit)? = null,
) {
    val text = pluralStringResource(labelRes, count, count)

    val modifier = Modifier
        .padding(8.dp)
        .wrapContentWidth()
        .then(
            if (onClick != null) {
                Modifier.clickable(onClick = onClick)
            } else {
                Modifier
            }
        )

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = if (onClick != null) {
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        modifier = modifier
    )
}


@Composable
private fun TopicSocialActions(
    topic: TopicDetail? = null,
    isLoggedIn: Boolean = false,
    onReact: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {

        if (isLoggedIn) {
            topic?.reactionType?.let { reactionType ->
                IconButton(
                    onClick = {
                        onReact(reactionType != ReactionType.TYPE_VOTE)
                    }) {
                    Icon(
                        imageVector = if (reactionType == ReactionType.TYPE_VOTE) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = null
                    )
                }
            }

            topic?.isCollected?.let { isCollected ->
                IconButton(
                    onClick = { },//TODO
                    enabled = false
                ) {
                    Icon(
                        imageVector = if (isCollected) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null
                    )
                }
            }

        }

        IconButton(onClick = {
            topic?.let { shareTopic(context, it) }
        }) {
            Icon(
                Icons.Filled.Share,
                contentDescription = null
            )
        }
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
        sortBy = TopicCommentSortBy.TOP
    ),
    ALL(
        textResId = R.string.all_comments,
        sortBy = TopicCommentSortBy.ALL
    );

    // Optional: Define custom display order if needed
    companion object {
        val displayOrder = listOf(TOP, ALL)
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
