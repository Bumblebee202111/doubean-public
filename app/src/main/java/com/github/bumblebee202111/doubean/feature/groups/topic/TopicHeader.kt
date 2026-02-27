package com.github.bumblebee202111.doubean.feature.groups.topic

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.PluralsRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.SmallGroupAvatar
import com.github.bumblebee202111.doubean.model.fangorns.ReactionType
import com.github.bumblebee202111.doubean.model.groups.TopicDetail
import com.github.bumblebee202111.doubean.ui.component.DateTimeText
import com.github.bumblebee202111.doubean.ui.component.UserProfileImage
import com.github.bumblebee202111.doubean.util.DateTimeStyle
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.toRelativeString
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@SuppressLint("ClickableViewAccessibility")
@Composable
fun TopicHeader(
    topic: TopicDetail,
    shouldShowPhotoList: Boolean?,
    contentHtml: String?,
    isLoggedIn: Boolean,
    onImageClick: (String) -> Unit,
    onGroupClick: (String, String?) -> Unit,
    onUserClick: (String) -> Unit,
    onReshareStatusesClick: (String) -> Unit,
    onOpenDeepLinkUrl: (String) -> Boolean,
    displayInvalidImageUrl: () -> Unit,
    onReact: (Boolean) -> Unit,
    onCollectActionInitiated: () -> Unit,
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
                    size = dimensionResource(id = R.dimen.icon_size_large),
                    onClick = { onUserClick(topic.author.id) }
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = topic.author.name,
                        modifier = Modifier.clickable { onUserClick(topic.author.id) },
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
            TopicSocialActions(
                topic = topic,
                isLoggedIn = isLoggedIn,
                onReact = onReact,
                onCollectClick = onCollectActionInitiated
            )
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
                fadeIn(animationSpec = tween(durationMillis = 300)).togetherWith(
                    fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                ) using
                        SizeTransform(clip = false)
            },
            label = "timeAnimation"
        ) { targetStateIsCreateTime ->
            val timeToShow = if (targetStateIsCreateTime) createTime else editTime
            val text = if (targetStateIsCreateTime) {
                timeToShow.toRelativeString(style = DateTimeStyle.FULL)
            } else {
                stringResource(
                    R.string.content_edited,
                    timeToShow.toRelativeString(style = DateTimeStyle.FULL)
                )
            }
            DateTimeText(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        DateTimeText(
            text = createTime.toRelativeString(style = DateTimeStyle.FULL),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
    onCollectClick: () -> Unit = {},
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
                    onClick = onCollectClick,
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

private fun shareTopic(context: Context, topic: TopicDetail) {
    val shareText = buildString {
        topic.group?.name?.let { append(it) }
        topic.tag?.let { append("|${it.name}") }
        if (topic.group != null || topic.tag != null) append(" ")

        append("@${topic.author.name}${context.getString(R.string.colon)} ${topic.title}\n${topic.url}")
    }
    ShareUtil.share(context = context, shareText = shareText)
}