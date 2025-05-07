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
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.SmallGroupAvatar
import com.github.bumblebee202111.doubean.feature.groups.shared.groupTopAppBarColor
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
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@Composable
fun TopicScreen(
    onBackClick: () -> Unit,
    onWebViewClick: (url: String) -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onReshareStatusesClick: (topicId: String) -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Unit,
    viewModel: TopicViewModel = hiltViewModel(),
) {
    val topic by viewModel.topic.collectAsStateWithLifecycle()
    val popularComments: List<TopicComment> by viewModel.popularComments.collectAsStateWithLifecycle()
    val allCommentLazyPagingItems = viewModel.allComments.collectAsLazyPagingItems()
    val shouldShowPhotoList by viewModel.shouldShowPhotoList.collectAsStateWithLifecycle()
    val contentHtml by viewModel.contentHtml.collectAsStateWithLifecycle()
    val commentSortBy by viewModel.commentsSortBy.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val groupColor = topic?.group?.color
    val shouldShowSpinner by viewModel.shouldShowSpinner.collectAsStateWithLifecycle()

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
        updateCommentSortBy = viewModel::updateCommentsSortBy,
        displayInvalidImageUrl = viewModel::displayInvalidImageUrl,
        onBackClick = onBackClick,
        onWebViewClick = onWebViewClick,
        onGroupClick = onGroupClick,
        onReshareStatusesClick = onReshareStatusesClick,
        // TODO
        // https://developer.android.google.cn/develop/ui/compose/touch-input/pointer-input/tap-and-press
        // shared element
        onImageClick = onImageClick,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl,
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
    updateCommentSortBy: (TopicCommentSortBy) -> Unit,
    displayInvalidImageUrl: () -> Unit,
    onBackClick: () -> Unit,
    onWebViewClick: (url: String) -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onReshareStatusesClick: (topicId: String) -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Unit,
    onReact: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    var shouldShowDialog by rememberSaveable { mutableStateOf(false) }

    var scrollToCommentItemIndex: Int? by remember {
        mutableStateOf(null)
    }
    val listState = rememberLazyListState()

    val itemCountBeforeComments = 2

    LaunchedEffect(scrollToCommentItemIndex) {
        val targetIndex = scrollToCommentItemIndex
        if (targetIndex != null) {
            val maxIndex = when (commentSortBy) {
                TopicCommentSortBy.TOP -> popularCommentsLazyPagingItems.size - 1
                TopicCommentSortBy.ALL -> allCommentLazyPagingItems.itemCount - 1
            }
            val clampedIndex = targetIndex.coerceIn(0, maxIndex.coerceAtLeast(0))

            listState.scrollToItem(itemCountBeforeComments + clampedIndex)

            if (commentSortBy == TopicCommentSortBy.ALL) {
                snapshotFlow { allCommentLazyPagingItems.loadState }
                    .awaitNotLoading()
                listState.scrollToItem(itemCountBeforeComments + clampedIndex)
            }
            scrollToCommentItemIndex = null
        }
    }

    Scaffold(
        topBar = {
            var appBarMenuExpanded by rememberSaveable { mutableStateOf(false) }
            var viewInMenuExpanded by rememberSaveable { mutableStateOf(false) }
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
                        val canJump = when (commentSortBy) {
                            TopicCommentSortBy.TOP -> popularCommentsLazyPagingItems.isNotEmpty()
                            TopicCommentSortBy.ALL -> allCommentLazyPagingItems.itemCount > 0
                        }
                        if (topic != null && canJump) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(R.string.jump_to_comment)) },
                                onClick = {
                                    appBarMenuExpanded = false
                                    shouldShowDialog = true
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
                                appBarMenuExpanded = false
                                viewInMenuExpanded = true
                            })
                    }

                    DropdownMenu(
                        expanded = viewInMenuExpanded,
                        onDismissRequest = { viewInMenuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.view_in_web)) },
                            onClick = {
                                viewInMenuExpanded = false
                                topic?.url?.let(onWebViewClick)
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.view_in_douban)) },
                            onClick = {
                                topic?.uri?.let {
                                    viewInMenuExpanded = false
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
        if (contentHtml != null && topic != null) {
            LazyColumn(
                contentPadding = paddingValues,
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {

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
                when (commentSortBy) {
                    TopicCommentSortBy.TOP -> {
                        items(
                            count = popularCommentsLazyPagingItems.size,
                            key = { popularCommentsLazyPagingItems[it].id },
                            contentType = { "TopicComment" }) { index ->
                            TopicComment(
                                comment = popularCommentsLazyPagingItems[index],
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
    val commentCount = remember(
        commentSortBy,
        popularCommentsLazyPagingItems,
        allCommentLazyPagingItems.itemCount
    ) {
        when (commentSortBy) {
            TopicCommentSortBy.TOP -> popularCommentsLazyPagingItems.size
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
}


@Composable
fun JumpToCommentOfIndexDialog(
    currentCommentIndex: Int, commentCount: Int, onDismissRequest: () -> Unit,
    jumpToCommentOfIndex: (newCommentIndex: Int) -> Unit,
) {
    if (commentCount <= 1) {
        onDismissRequest()
        return
    }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            JumpToCommentOfIndexSlider(
                initialCommentIndex = currentCommentIndex,
                maxCommentIndex = commentCount - 1,
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
    initialCommentIndex: Int,
    maxCommentIndex: Int,
    onTargetCommentIndexConfirmed: (newCommentIndex: Int) -> Unit,
) {
    var sliderPosition by rememberSaveable(initialCommentIndex) {
        mutableFloatStateOf(
            initialCommentIndex.toFloat()
        )
    }
    val steps = (maxCommentIndex - 1).coerceAtLeast(0)
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.jump_to_comment),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            steps = steps,
            valueRange = 0f..maxCommentIndex.toFloat(),
            onValueChangeFinished = {
                onTargetCommentIndexConfirmed(sliderPosition.toInt())
            },
        )
        Text(
            text = "${sliderPosition.toInt() + 1} / ${maxCommentIndex + 1}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
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
            Row(
                Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserProfileImage(
                    url = topic.author.avatar,
                    size = dimensionResource(id = R.dimen.icon_size_large)
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = topic.author.name,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TopicTimeDisplay(
                            createTime = topic.createTime,
                            editTime = topic.editTime
                        )
                        topic.ipLocation?.let {
                            Text(
                                text = " · $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = dimensionResource(R.dimen.margin_extra_small))
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            val showPhotos = remember(shouldShowPhotoList, topic.images) {
                shouldShowPhotoList == true && !topic.images.isNullOrEmpty()
            }
            if (showPhotos) {
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
            Spacer(Modifier.height(12.dp))
            Text(
                text = topic.title,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.headlineSmall
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
                        horizontal = 12.dp,
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
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
                        labelRes = R.plurals.collections
                    )
                }
            }
            TopicSocialActions(topic = topic, isLoggedIn = isLoggedIn, onReact = onReact)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TopicTimeDisplay(createTime: LocalDateTime, editTime: LocalDateTime?) {
    var showCreateTime by rememberSaveable { mutableStateOf(true) }

    if (editTime != null && editTime != createTime) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000L)
                showCreateTime = !showCreateTime
            }
        }

        AnimatedContent(
            targetState = showCreateTime,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 300)) with
                        fadeOut(animationSpec = tween(durationMillis = 300)) using
                        SizeTransform(clip = false)
            },
            label = "timeAnimation"
        ) { targetStateIsCreateTime ->
            val timeToShow = if (targetStateIsCreateTime) createTime else editTime
            val text = if (targetStateIsCreateTime) {
                timeToShow.fullDateTimeString()
            } else {
                stringResource(R.string.content_edited, timeToShow.fullDateTimeString())
            }
            DateTimeText(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        DateTimeText(
            text = createTime.fullDateTimeString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
                            val imageUrl = webViewHitTestResult.extra
                            if (imageUrl != null && URLUtil.isValidUrl(imageUrl)) {
                                val largeImageUrl = topic.images
                                    ?.firstOrNull { it.normal.url == imageUrl }
                                    ?.large?.url
                                    ?: imageUrl
                                onImageClick(largeImageUrl)
                                return@setOnTouchListener true
                            } else if (imageUrl != null) {
                                Log.w("ContentWebView", "Invalid image URL clicked: $imageUrl")
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
                    return url?.let { handleUrlLoading(context, it, onOpenDeepLinkUrl) } ?: false
                }
            }
        },
    )
}

private fun handleUrlLoading(
    context: Context,
    url: String,
    onOpenDeepLinkUrl: (String) -> Unit,
): Boolean {
    Log.d("ContentWebView", "Link clicked: $url")
    try {
        onOpenDeepLinkUrl(url)
        return true
    } catch (_: IllegalArgumentException) {
        Log.i("ContentWebView", "Not a deep link, opening externally: $url")
        OpenInUtils.viewInActivity(context, url)
        return true
    } catch (e: Exception) {
        Log.e("ContentWebView", "Error handling URL click: $url", e)
        return false
    }
}

@Composable
private fun CountItem(
    count: Int,
    @PluralsRes labelRes: Int,
    onClick: (() -> Unit)? = null,
) {
    val text = pluralStringResource(labelRes, count, count)
    val isClickable = onClick != null
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = if (isClickable) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .then(if (isClickable) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .wrapContentWidth()
    )
}


@Composable
private fun TopicSocialActions(
    topic: TopicDetail? = null,
    isLoggedIn: Boolean = false,
    onReact: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        if (isLoggedIn && topic != null) {
            topic.reactionType?.let { reactionType ->
                IconButton(
                    onClick = {
                        onReact(reactionType != ReactionType.TYPE_VOTE)
                    }) {
                    val isVoted = topic.reactionType == ReactionType.TYPE_VOTE
                    Icon(
                        imageVector = if (reactionType == ReactionType.TYPE_VOTE) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = null,
                        tint = if (isVoted) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }
            }

            topic.isCollected?.let { isCollected ->
                IconButton(
                    onClick = {/* TODO: Implement Save/Unsave action */ },
                    enabled = false //// Disabled for now
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
    val shareText = buildString {
        topic.group?.name?.let { append(it) }
        topic.tag?.let { append("|${it.name}") }
        if (topic.group != null || topic.tag != null) append(" ")

        append("@${topic.author.name}${context.getString(R.string.colon)} ${topic.title}\n${topic.url}")
    }
    ShareUtil.share(context = context, shareText = shareText)
}
