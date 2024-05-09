@file:Suppress("DEPRECATION")

package com.github.bumblebee202111.doubean.feature.groups.postDetail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import androidx.paging.awaitNotLoading
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.MobileNavigationDirections
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.FragmentPostDetailBinding
import com.github.bumblebee202111.doubean.databinding.ListItemPostCommentBinding
import com.github.bumblebee202111.doubean.model.PostComment
import com.github.bumblebee202111.doubean.model.PostCommentSortBy
import com.github.bumblebee202111.doubean.model.PostDetail
import com.github.bumblebee202111.doubean.model.SizedPhoto
import com.github.bumblebee202111.doubean.ui.common.DoubeanWebView
import com.github.bumblebee202111.doubean.ui.common.TopicWebViewClient
import com.github.bumblebee202111.doubean.ui.common.bindAvatarFromUrl
import com.github.bumblebee202111.doubean.ui.common.bindDateTimeStringAndStyle
import com.github.bumblebee202111.doubean.ui.component.ListItemImages
import com.github.bumblebee202111.doubean.ui.theme.AppTheme
import com.github.bumblebee202111.doubean.util.DateTimeStyle
import com.github.bumblebee202111.doubean.util.OpenInUtil
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.TOPIC_CSS_FILENAME
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberSaveableWebViewState
import com.google.accompanist.web.rememberWebViewNavigator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private val postDetailViewModel: PostDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            //val navController = findNavController()
            setContent {
                AppTheme {
                    PostDetailScreen(
                        postDetailViewModel = postDetailViewModel,
                        updateCommentSortBy = { postDetailViewModel.updateCommentsSortBy(it) },
                        onBackClick = { findNavController().popBackStack() },
                        onTopicShareClick = { topic ->
                            val shareText = StringBuilder()
                            topic.group?.name.let { groupName ->
                                shareText.append(groupName)
                            }
                            topic.tag?.let { tag -> shareText.append("|" + tag.name) }
                            shareText.append("@${topic.author.name}： ${topic.title}${topic.url}${topic.content}")
                            ShareUtil.share(requireContext(), shareText)
                        },
                        navigateToWebView = { url ->
                            findNavController().navigate(
                                MobileNavigationDirections.actionGlobalNavWebView(
                                    url
                                )
                            )
                        },
                        navigateToGroup = { groupId, tabId ->
                            findNavController().navigate(
                                PostDetailFragmentDirections.actionPostDetailToGroupDetail(groupId)
                                    .setDefaultTabId(tabId)
                            )
                        },
                        navigateToImage = { url ->
                            findNavController().navigate(
                                MobileNavigationDirections.actionGlobalNavImage(
                                    url
                                )
                            )
                            //TODO https://developer.android.google.cn/develop/ui/compose/touch-input/pointer-input/tap-and-press
                            // shared element
                        },
                        navigateWithDeepLinkUrl = { url ->
                            val request =
                                NavDeepLinkRequest.Builder.fromUri(url.toUri()).build()
                            findNavController().navigate(request)
                        },
                        onShowToast = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        },
                        viewInDouban = { uri ->
                            OpenInUtil.openInDouban(requireContext(), uri)
                        }
                    ) { url ->
                        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            startActivity(this)
                        }
                    }
                }

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PostDetailScreen(
        postDetailViewModel: PostDetailViewModel,
        updateCommentSortBy: (PostCommentSortBy) -> Unit,
        onBackClick: () -> Unit,
        onTopicShareClick: (PostDetail) -> Unit,
        navigateToWebView: (url: String) -> Unit,
        navigateToGroup: (groupId: String, tabId: String?) -> Unit,
        navigateToImage: (url: String) -> Unit,
        navigateWithDeepLinkUrl: (url: String) -> Unit,
        onShowToast: (text: String) -> Unit,
        viewInDouban: (uri: String) -> Unit,
        viewInActivity: (url: String) -> Unit,
    ) {
        val topic by postDetailViewModel.topic.collectAsStateWithLifecycle()
        val commentLazyPagingItems = postDetailViewModel.comments.collectAsLazyPagingItems()
        val contentHtml by postDetailViewModel.contentHtml.collectAsStateWithLifecycle()
        val commentSortBy by postDetailViewModel.commentsSortBy.collectAsStateWithLifecycle()
        val groupColorInt = topic?.group?.color

        var shouldShowDialog by remember {
            mutableStateOf(false)
        }
        var scrollToCommentItemIndex: Int? by remember {
            mutableStateOf(null)
        }
        val listState = rememberLazyListState()
        val itemCountBeforeComments = 1 + if (topic == null) 0 else 1

        scrollToCommentItemIndex?.let { index ->
            LaunchedEffect(index) {
                listState.scrollToItem(itemCountBeforeComments + index)
                snapshotFlow {
                    commentLazyPagingItems.loadState
                }.awaitNotLoading()
                listState.scrollToItem(itemCountBeforeComments + index)
                scrollToCommentItemIndex = null
            }
        }

        Scaffold(
            topBar = {
                var appBarMenuExpanded by remember { mutableStateOf(false) }
                var viewInMenuExpanded by remember { mutableStateOf(false) }
                val groupColor = groupColorInt?.let(::Color)
                TopAppBar(title = {
                    topic?.title?.let {
                        Text(
                            text = it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            Modifier.clickable(onClick = onBackClick)
                        )
                    },
                    actions = {

                        IconButton(onClick = {
                            topic?.let { onTopicShareClick(it) }

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
                            DropdownMenuItem(text = {
                                Text(text = "Jump to comment")
                            },
                                onClick = {
                                    appBarMenuExpanded = false
                                    shouldShowDialog = true
                                })

                            DropdownMenuItem(text = {
                                Text(text = getString(R.string.view_in))
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
                                text = { Text(getString(R.string.view_in_web)) },
                                onClick = { topic?.url?.let(navigateToWebView) })
                            DropdownMenuItem(
                                text = { Text(getString(R.string.view_in_douban)) },
                                onClick = { topic?.uri?.let(viewInDouban) })
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
                            viewInActivity = viewInActivity,
                            navigateToImage = navigateToImage,
                            navigateToGroup = navigateToGroup,
                            navigateWithDeepLinkUrl = navigateWithDeepLinkUrl,
                            onShowToast = onShowToast
                        )
                    }
                }

                item(key = "TopicCommentSortBy", contentType = "TopicCommentSortBy") {
                    TopicCommentSortBy(
                        commentSortBy = commentSortBy,
                        updateCommentSortBy = updateCommentSortBy
                    )
                }

                items(
                    count = commentLazyPagingItems.itemCount,
                    key = commentLazyPagingItems.itemKey { it.id },
                    contentType = commentLazyPagingItems.itemContentType { "TopicCommentAndroidView" }) { index ->
                    TopicCommentAndroidView(
                        comment = commentLazyPagingItems[index],
                        groupColorInt = groupColorInt,
                        topic = topic,
                        navigateToImage = navigateToImage
                    )
                    if (index < commentLazyPagingItems.itemCount - 1)
                        HorizontalDivider(thickness = 1.dp)
                }
            }
        }
        topic?.commentCount?.let {
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
            steps = steps,
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
    topic: PostDetail,
    contentHtml: String?,
    viewInActivity: (url: String) -> Unit,
    navigateToImage: (url: String) -> Unit,
    navigateToGroup: (groupId: String, tabId: String?) -> Unit,
    navigateWithDeepLinkUrl: (url: String) -> Unit,
    onShowToast: (text: String) -> Unit,
) {
    Column {
        AndroidViewBinding(
            factory = { inflater, root, attachToRoot ->
                FragmentPostDetailBinding.inflate(
                    inflater,
                    root,
                    attachToRoot
                )
            },
            modifier = Modifier,
            //.fillMaxSize()
            onReset = {},
            //onRelease = {}
        ) {

            postTag.setOnClickListener {
                topic.group?.let { group ->
                    navigateToGroup(
                        group.id,
                        topic.id
                    )
                }

            }

            val groupOnClickListener = View.OnClickListener {
                topic.group?.let { group ->
                    navigateToGroup(
                        group.id, null
                    )
                }
            }

            groupName.setOnClickListener(groupOnClickListener)
            groupAvatar.setOnClickListener(groupOnClickListener)

            this.topic = topic

            this.content.setContent {
                contentHtml?.let {

                    val webViewState = rememberSaveableWebViewState()
                    val navigator = rememberWebViewNavigator()

                    LaunchedEffect(navigator) {
                        val bundle = webViewState.viewState
                        if (bundle == null) {
                            // This is the first time load, so load the home page.
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
                                        navigateWithDeepLinkUrl(url)
                                    } catch (e: IllegalArgumentException) {
                                        Log.i("doubean", "shouldOverrideUrlLoading: $e")
                                        viewInActivity(url)
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
                                            if (URLUtil.isValidUrl(imageUrl)) {
                                                val largeImageUrl =
                                                    topic.images!!.first { it.normal.url == imageUrl }.large.url
                                                navigateToImage(largeImageUrl)
                                            } else {
                                                onShowToast("Invalid Image Url")
                                            }
                                        }
                                    }

                                    return@setOnTouchListener false
                                }
                            }
                        })


                }
            }
            executePendingBindings()
        }
    }
}

@Composable
fun TopicCommentSortBy(
    commentSortBy: PostCommentSortBy,
    updateCommentSortBy: (PostCommentSortBy) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val sortCommentsByLabels =
        stringArrayResource(id = R.array.sort_comments_by_array)

    val indexToSortBy = mapOf(
        0 to PostCommentSortBy.TOP,
        1 to PostCommentSortBy.ALL
    )

    val sortByToIndex = mapOf(
        PostCommentSortBy.TOP to 0,
        PostCommentSortBy.ALL to 1
    )

    Box(
        Modifier.padding(
            start = dimensionResource(id = R.dimen.margin_normal),
            end = dimensionResource(id = R.dimen.margin_normal),
            top = dimensionResource(id = R.dimen.margin_small)
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

@Composable
fun TopicCommentAndroidView(
    comment: PostComment?,
    groupColorInt: Int?,
    topic: PostDetail?,
    navigateToImage: (url: String) -> Unit,
) {

    AndroidViewBinding(
        factory = ListItemPostCommentBinding::inflate,
        //Modifier.fillMaxSize(),
        update = {
            authorAvatar.bindAvatarFromUrl(comment?.author?.avatarUrl)
            authorName.text = comment?.author?.name
            authorMiddleDot.isVisible = comment?.author != null
            created.bindDateTimeStringAndStyle(comment?.created, DateTimeStyle.Normal)
            ipLocation.text = comment?.ipLocation
            more.setOnClickListener { v ->
                val context = v.context
                val popupMenu = PopupMenu(context, more)
                popupMenu.inflate(R.menu.menu_post_item)
                popupMenu.setOnMenuItemClickListener { item ->
                    comment?.let { comment ->
                        when (item.itemId) {
                            R.id.action_share -> {
                                val shareText = StringBuilder()
                                topic?.let {
                                    it.group?.let { group -> shareText.append(group.name) }
                                    it.tag?.let { tag ->
                                        shareText.append("|" + tag.name)
                                    }
                                }

                                shareText.append(
                                    "@${comment.author.name}${
                                        context.getString(R.string.colon)
                                    }${comment.text}"
                                )

                                comment.repliedTo?.let { repliedTo ->
                                    shareText.append("${context.getString(R.string.repliedTo)}@${repliedTo.author.name}： ${repliedTo.text}")
                                }
                                topic?.let { topic ->
                                    shareText.append(
                                        """${context.getString(R.string.post)}@${topic.author.name}${
                                            context.getString(R.string.colon)
                                        }
                                            ${topic.title} ${topic.url}
                                            """
                                    )
                                }
                                ShareUtil.share(context, shareText)
                                true
                            }

                            else -> false
                        }

                    }
                    false
                }
                popupMenu.show()
            }
            comment?.photos.takeUnless { it.isNullOrEmpty() }?.let {
                photos.apply {
                    //setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        ListItemImages(
                            images = it.map(SizedPhoto::image),
                            onImageClick = { navigateToImage(it.large.url) }
                        )
                    }
                }
            }

            commentText.apply {
                text = comment?.text
                isVisible = comment == null || !comment.text.isNullOrBlank()
            }

            authorOp.isVisible = comment?.author?.id?.let { it == topic?.author?.id } ?: false
            repliedTo.isVisible = comment?.repliedTo != null

            repliedToAvatar.bindAvatarFromUrl(comment?.repliedTo?.author?.avatarUrl)
            repliedToAuthorOp.isVisible =
                comment?.repliedTo?.author?.id?.let { it == topic?.author?.id } ?: false
            repliedToAuthorName.text = comment?.repliedTo?.author?.name
            repliedToCreated.bindDateTimeStringAndStyle(
                comment?.repliedTo?.created,
                DateTimeStyle.Normal
            )
            repliedToMiddleDot.isVisible = comment?.repliedTo?.author != null
            repliedToText.apply {
                text = comment?.repliedTo?.text
                isVisible = !comment?.repliedTo?.text.isNullOrBlank()
            }
            comment?.repliedTo?.photos.takeUnless { it.isNullOrEmpty() }?.let {
                repliedToPhotos.apply {
                    //setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        ListItemImages(
                            images = it.map(SizedPhoto::image),
                            onImageClick = { navigateToImage(it.large.url) }
                        )
                    }
                }
            }

            groupColorInt?.let {
                authorOp.setTextColor(it)
                repliedToAuthorOp.setTextColor(it)
            }



            likeIcon.isVisible = comment?.voteCount?.takeIf { it != 0 } != null
            likeCount.isVisible = comment?.voteCount?.takeIf { it != 0 } != null
            likeCount.text = comment?.voteCount?.toString()
        },
        onReset = {},
        onRelease = {}
    )

}

