package com.github.bumblebee202111.doubean.feature.groups.topic

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.TopicActivityItemUserProfileImage
import com.github.bumblebee202111.doubean.model.SizedImage
import com.github.bumblebee202111.doubean.model.SizedPhoto
import com.github.bumblebee202111.doubean.model.fangorns.User
import com.github.bumblebee202111.doubean.model.groups.TopicComment
import com.github.bumblebee202111.doubean.model.groups.TopicDetail
import com.github.bumblebee202111.doubean.model.groups.TopicRefComment
import com.github.bumblebee202111.doubean.ui.component.DateTimeText
import com.github.bumblebee202111.doubean.ui.component.ListItemCount
import com.github.bumblebee202111.doubean.ui.component.ListItemImages
import com.github.bumblebee202111.doubean.ui.component.UserProfileImage
import com.github.bumblebee202111.doubean.util.DateTimeStyle
import com.github.bumblebee202111.doubean.util.toColorOrPrimary
import com.github.bumblebee202111.doubean.util.toRelativeString
import java.time.LocalDateTime

@Composable
fun TopicComment(
    comment: TopicComment,
    topic: TopicDetail,
    onUserClick: (id: String) -> Unit,
    onImageClick: (url: String) -> Unit,
) {
    var showActionsBottomSheet by remember { mutableStateOf(false) }

    if (showActionsBottomSheet) {
        TopicCommentActionsBottomSheet(
            comment = comment,
            topic = topic,
            onDismissRequest = { showActionsBottomSheet = false }
        )
    }

    val groupThemeColor = topic.group?.color.toColorOrPrimary()
    val isOp = remember(comment.author.id, topic.author.id) {
        comment.author.id == topic.author.id
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp)
    ) {
        Row(
            Modifier
                .padding(vertical = 12.dp)
                .weight(1f)
        ) {
            TopicActivityItemUserProfileImage(
                url = comment.author.avatar,
                onClick = { onUserClick(comment.author.id) })
            Spacer(modifier = Modifier.width(8.dp))
            Column(Modifier.fillMaxWidth()) {
                TopicCommentHeaderRow(
                    author = comment.author,
                    isOp = isOp,
                    groupColor = groupThemeColor,
                    createTime = comment.createTime,
                    ipLocation = comment.ipLocation,
                    showAuthorAvatar = false,
                    onAuthorClick = onUserClick
                )
                comment.refComment?.let { refComment ->
                    Spacer(modifier = Modifier.height(4.dp))
                    TopicRefCommentCard(
                        refComment = refComment,
                        topicAuthorId = topic.author.id,
                        groupColor = groupThemeColor,
                        onAuthorClick = onUserClick,
                        onImageClick = onImageClick
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = comment.text ?: "", style = MaterialTheme.typography.bodyMedium)
                comment.photos.takeUnless(List<SizedPhoto>?::isNullOrEmpty)?.let { photos ->
                    val images: List<SizedImage> = remember(photos) { photos.map { it.image } }
                    Spacer(modifier = Modifier.height(12.dp))
                    ListItemImages(
                        images = images,
                        onImageClick = { image -> onImageClick(image.large.url) }
                    )
                }
                comment.voteCount.takeIf { it > 0 }?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    ListItemCount(iconVector = Icons.Outlined.ThumbUp, count = it)
                }
            }
        }

        IconButton(
            onClick = { showActionsBottomSheet = true },
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.more)
            )
        }
    }

}

@Composable
fun TopicCommentPlaceholder(
    modifier: Modifier = Modifier,
) {
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(placeholderColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(14.dp)
                    .background(placeholderColor, MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(14.dp)
                    .background(placeholderColor, MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .background(placeholderColor, MaterialTheme.shapes.small)
            )
        }
    }
}

@Composable
private fun TopicCommentHeaderRow(
    author: User?,
    isOp: Boolean,
    groupColor: Color,
    createTime: LocalDateTime?,
    ipLocation: String?,
    showAuthorAvatar: Boolean,
    onAuthorClick: (id: String) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (showAuthorAvatar) {
            UserProfileImage(url = author?.avatar, size = 16.dp)
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = author?.name ?: "",
            modifier = Modifier
                .weight(weight = 1f, fill = false)
                .clickable(enabled = author?.id != null) {
                    author?.id?.let(onAuthorClick)
                },
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge
        )
        if (isOp) {
            Text(
                text = stringResource(id = R.string.op),
                modifier = Modifier.padding(start = 4.dp),
                color = groupColor,
                style = MaterialTheme.typography.labelSmall
            )
        }
        createTime?.let {
            Text(
                text = stringResource(id = R.string.middle_dot),
                modifier = Modifier.padding(horizontal = 4.dp),
                style = MaterialTheme.typography.labelLarge
            )
            DateTimeText(
                text = it.toRelativeString(style = DateTimeStyle.INTERMEDIATE),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        ipLocation?.takeIf { it.isNotBlank() }?.let { loc ->
            Text(
                text = stringResource(id = R.string.middle_dot),
                modifier = Modifier.padding(horizontal = 4.dp),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = loc,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TopicRefCommentCard(
    refComment: TopicRefComment,
    topicAuthorId: String,
    groupColor: Color,
    onAuthorClick: (id: String) -> Unit,
    onImageClick: (url: String) -> Unit,
) {
    val isRefOp = remember(refComment.author.id, topicAuthorId) {
        refComment.author.id == topicAuthorId
    }
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            TopicCommentHeaderRow(
                author = refComment.author,
                isOp = isRefOp,
                groupColor = groupColor,
                createTime = refComment.createTime,
                ipLocation = refComment.ipLocation,
                showAuthorAvatar = true,
                onAuthorClick = onAuthorClick,
            )
            refComment.text?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = it, style = MaterialTheme.typography.bodyMedium)

            }
            refComment.photos?.takeUnless(List<SizedPhoto>?::isNullOrEmpty)?.let { photos ->
                val images = remember(photos) { photos.map { it.image } }
                Spacer(modifier = Modifier.height(8.dp))
                ListItemImages(
                    images = images,
                    onImageClick = { image -> onImageClick(image.large.url) }
                )
            }
            refComment.voteCount.takeIf { it > 0 }?.let {
                Spacer(modifier = Modifier.height(8.dp))
                ListItemCount(iconVector = Icons.Outlined.ThumbUp, count = it)
            }
        }
    }
}
