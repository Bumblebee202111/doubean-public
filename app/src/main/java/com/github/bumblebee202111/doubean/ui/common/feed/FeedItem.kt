package com.github.bumblebee202111.doubean.ui.common.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.Photo
import com.github.bumblebee202111.doubean.model.common.BaseFeedableItem
import com.github.bumblebee202111.doubean.model.common.FeedItem
import com.github.bumblebee202111.doubean.model.common.FeedItemLayout
import com.github.bumblebee202111.doubean.model.common.ReviewFeedContent
import com.github.bumblebee202111.doubean.model.common.SubjectFeedContent
import com.github.bumblebee202111.doubean.model.common.TopicFeedContent
import com.github.bumblebee202111.doubean.model.fangorns.User
import com.github.bumblebee202111.doubean.model.subjects.MarkableSubject
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectItem
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectItemBasicContent
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectSimpleInterestButton
import com.github.bumblebee202111.doubean.ui.component.ListItemImages
import com.github.bumblebee202111.doubean.util.abbreviatedDateTimeString
import java.time.LocalDateTime


@Composable
fun FeedItem(
    feedItem: FeedItem<*>,
    isLoggedIn: Boolean,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onMarkSubject: (MarkableSubject) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {

        val owner = feedItem.owner
        val createTime = feedItem.createTime
        val content = feedItem.content
        val layout = feedItem.layout

        val hasHeaderAndFooter = layout in setOf(
            FeedItemLayout.LAYOUT_STATUS,
            FeedItemLayout.LAYOUT_DEFAULT_CONTENT_RECTANGLE,
            FeedItemLayout.LAYOUT_ALBUM,
            FeedItemLayout.LAYOUT_FOLD_PHOTO,
            FeedItemLayout.LAYOUT_VIDEO_DEFAULT,
            FeedItemLayout.LAYOUT_PODCAST_EPISODE,
        )

        if (hasHeaderAndFooter && owner != null && createTime != null) {
            FeedItemHeader(
                owner = owner,
                createTime = createTime,
                modifier = Modifier.fillMaxWidth(),
                onUserClick = onUserClick
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            when {
                layout == FeedItemLayout.LAYOUT_DEFAULT_CONTENT_RECTANGLE && content is TopicFeedContent -> {
                    TopicFeedContent(content = content, onImageClick = onImageClick)
                }

                layout == FeedItemLayout.LAYOUT_DEFAULT_CONTENT_RECTANGLE && content is ReviewFeedContent -> {
                    ReviewFeedContent(content = content, onImageClick = onImageClick)
                }

                layout == FeedItemLayout.LAYOUT_SUBJECT_LAYOUT && content is SubjectFeedContent -> {
                    SubjectFeedContent(
                        content = content,
                        isLoggedIn = isLoggedIn,
                        onMarkClick = onMarkSubject
                    )
                }

                else -> {
                    GenericUnsupportedContent(
                        contentType = feedItem.content.type,
                        itemId = feedItem.id,
                        itemType = feedItem.type,
                        layoutName = feedItem.layout.name
                    )
                }
            }
        }

        if (hasHeaderAndFooter && (feedItem.commentsCount > 0 || feedItem.reactionsCount > 0 || feedItem.resharesCount > 0)) {
            Spacer(modifier = Modifier.height(8.dp))
            FeedItemFooter(
                itemType = feedItem.type,
                commentsCount = feedItem.commentsCount,
                reactionsCount = feedItem.reactionsCount,
                resharesCount = feedItem.resharesCount,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
private fun FeedItemHeader(
    owner: User,
    createTime: LocalDateTime,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(owner.avatar)
                .crossfade(true).build(),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable { onUserClick(owner.id) },
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = owner.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable { onUserClick(owner.id) }
        )
        Text(
            text = createTime.abbreviatedDateTimeString(LocalContext.current),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun FeedItemFooter(
    itemType: String,
    commentsCount: Int,
    reactionsCount: Int,
    resharesCount: Int,
    modifier: Modifier = Modifier,
) {
    val parts = mutableListOf<String>()

    if (commentsCount > 0) {
        parts.add(stringResource(R.string.ugc_comments, commentsCount))
    }

    if (reactionsCount > 0) {
        val reactionsCountStringResId = if (itemType == BaseFeedableItem.TYPE_REVIEW) {
            R.string.ugc_like_review
        } else {
            R.string.ugc_like
        }
        parts.add(stringResource(reactionsCountStringResId, reactionsCount))
    }

    if (resharesCount > 0) {
        parts.add(stringResource(R.string.ugc_reshares, resharesCount))
    }

    if (parts.isNotEmpty()) {
        Text(
            text = parts.joinToString(" · "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
    }
}


@Composable
private fun TopicFeedContent(content: TopicFeedContent, onImageClick: (String) -> Unit) {
    ArticleContent(
        title = content.title,
        abstractString = content.abstractString,
        photos = content.photos,
        onImageClick = onImageClick
    )
}

@Composable
private fun SubjectFeedContent(
    content: SubjectFeedContent,
    isLoggedIn: Boolean,
    onMarkClick: (MarkableSubject) -> Unit,
) {
    val subject = content.subject
    SubjectItem(
        basicContent = {
            SubjectItemBasicContent(subject = content.subject.subject)
        },
        interestButton = when (isLoggedIn) {
            true -> {
                {
                    SubjectSimpleInterestButton(
                        subjectType = subject.type,
                        interest = subject.interest,
                        onMarkClick = {
                            onMarkClick(subject)
                        }
                    )
                }
            }

            false -> null
        })
}

@Composable
private fun ReviewFeedContent(content: ReviewFeedContent, onImageClick: (String) -> Unit) {
    ArticleContent(
        title = content.title,
        abstractString = content.abstractString,
        photos = content.photos,
        onImageClick = onImageClick
    )
}

@Composable
private fun GenericUnsupportedContent(
    contentType: String,
    itemId: String,
    itemType: String,
    layoutName: String,
) {
    Column {
        Text(
            text = stringResource(R.string.unsupported_content_label, contentType),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(
                R.string.unsupported_content_more_info,
                itemId,
                itemType,
                layoutName
            ),
            style = MaterialTheme.typography.bodySmall,
            color = LocalContentColor.current.copy(alpha = 0.7f)
        )
    }
}


@Composable
private fun ArticleContent(
    title: String,
    abstractString: String,
    photos: List<Photo>,
    modifier: Modifier = Modifier,
    maxTitleLines: Int = Int.MAX_VALUE,
    maxAbstractLines: Int = Int.MAX_VALUE,
    onImageClick: (String) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = maxTitleLines,
            overflow = TextOverflow.Ellipsis
        )
        abstractString.takeIf { it.isNotBlank() }?.let { nonBlankAbstractString ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = nonBlankAbstractString,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = maxAbstractLines,
                overflow = TextOverflow.Ellipsis
            )
        }
        photos.takeIf { it.isNotEmpty() }?.let { nonEmptyPhotos ->
            Spacer(modifier = Modifier.height(8.dp))
            ListItemImages(
                images = nonEmptyPhotos.map { it.image },
                modifier = Modifier.fillMaxWidth(),
                onImageClick = { onImageClick(it.large.url) }
            )
        }
    }
}