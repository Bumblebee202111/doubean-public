package com.github.bumblebee202111.doubean.feature.groups.topic

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.TopicActivityItemUserProfileImage
import com.github.bumblebee202111.doubean.model.SizedPhoto
import com.github.bumblebee202111.doubean.model.User
import com.github.bumblebee202111.doubean.model.groups.TopicComment
import com.github.bumblebee202111.doubean.model.groups.TopicDetail
import com.github.bumblebee202111.doubean.model.groups.TopicRefComment
import com.github.bumblebee202111.doubean.ui.component.DateTimeText
import com.github.bumblebee202111.doubean.ui.component.ListItemCount
import com.github.bumblebee202111.doubean.ui.component.ListItemImages
import com.github.bumblebee202111.doubean.ui.component.UserProfileImage
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.intermediateDateTimeString
import com.github.bumblebee202111.doubean.util.toColorOrPrimary
import java.time.LocalDateTime


@Composable
fun TopicComment(
    comment: TopicComment,
    groupColor: String?,
    topic: TopicDetail,
    onImageClick: (url: String) -> Unit,
) {

    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            Modifier
                .padding(vertical = 12.dp)
                .weight(1f)
        ) {
            TopicActivityItemUserProfileImage(url = comment.author.avatar)
            Spacer(modifier = Modifier.width(8.dp))
            Column(Modifier.fillMaxWidth()) {
                
                TopicCommentHeaderRow(
                    author = comment.author,
                    topic = topic,
                    groupColor = groupColor,
                    createTime = comment.createTime,
                    ipLocation = comment.ipLocation,
                    showAuthorAvatar = false
                )
                comment.refComment?.let { refComment ->
                    Spacer(modifier = Modifier.height(4.dp))
                    TopicRefCommentCard(
                        refComment = refComment,
                        topic = topic,
                        groupColor = groupColor,
                        onImageClick = onImageClick
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = comment.text ?: "", style = MaterialTheme.typography.bodyMedium)
                comment.photos.takeUnless(List<SizedPhoto>?::isNullOrEmpty)?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    ListItemImages(
                        images = it.map(SizedPhoto::image),
                        onImageClick = { image -> onImageClick(image.large.url) }
                    )
                }
                comment.voteCount.takeIf { it != 0 }?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    ListItemCount(iconVector = Icons.Outlined.ThumbUp, count = it)
                }
            }
        }

        Box(modifier = Modifier.padding(all = 2.dp)) {
            var expanded by remember { mutableStateOf(false) }

            IconButton(onClick = { expanded = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.share)) },
                    onClick = {
                        val shareText = createTopicCommentShareText(
                            comment = comment,
                            topic = topic,
                            context = context
                        )

                        ShareUtil.share(context, shareText)
                    })
            }
        }
    }

}

@Composable
private fun TopicCommentHeaderRow(
    author: User?,
    topic: TopicDetail,
    groupColor: String?,
    createTime: LocalDateTime?,
    ipLocation: String?,
    showAuthorAvatar: Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (showAuthorAvatar) {
            UserProfileImage(url = author?.avatar, size = 16.dp)
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = author?.name ?: "",
            modifier = Modifier.weight(weight = 1f, fill = false),
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge
        )
        if (author?.id == topic.author.id) {
            Text(
                text = stringResource(id = R.string.op),
                modifier = Modifier.padding(start = 4.dp),
                color = groupColor.toColorOrPrimary(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        createTime?.let {
            Text(
                text = stringResource(id = R.string.middle_dot),
                modifier = Modifier.padding(
                    horizontal = 4.dp
                )
            )
            DateTimeText(text = it.intermediateDateTimeString())
        }
        Spacer(modifier = Modifier.width(4.dp))
        if (ipLocation != null) {
            Text(text = ipLocation, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TopicRefCommentCard(
    refComment: TopicRefComment,
    topic: TopicDetail,
    groupColor: String?,
    onImageClick: (url: String) -> Unit,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            TopicCommentHeaderRow(
                author = refComment.author,
                topic = topic,
                groupColor = groupColor,
                createTime = refComment.createTime,
                ipLocation = refComment.ipLocation,
                showAuthorAvatar = true,
            )
            refComment.text?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = it, style = MaterialTheme.typography.bodyMedium)

            }
            refComment.photos?.takeUnless(List<SizedPhoto>?::isNullOrEmpty)?.let {
                Spacer(modifier = Modifier.height(4.dp))
                ListItemImages(
                    images = it.map(SizedPhoto::image),
                    onImageClick = { image -> onImageClick(image.large.url) }
                )
            }
            refComment.voteCount.takeIf { it != 0 }?.let {
                Spacer(modifier = Modifier.height(8.dp))
                ListItemCount(iconVector = Icons.Outlined.ThumbUp, count = it)
            }
        }
    }
}

private fun createTopicCommentShareText(
    comment: TopicComment,
    topic: TopicDetail,
    context: Context,
): String {
    return buildString {
        topic.group?.let { group -> append(group.name) }
        topic.tag?.let { tag ->
            append("|" + tag.name)
        }

        append(
            "@${comment.author.name}${
                context.getString(R.string.colon)
            }${comment.text}"
        )
        comment.refComment?.let { repliedTo ->
            append("${context.getString(R.string.repliedTo)}@${repliedTo.author.name}： ${repliedTo.text}")
        }
        append(
            "${context.getString(R.string.topic)}@${topic.author.name}${
                context.getString(R.string.colon)
            }\n${topic.title} ${topic.url}\n"
        )

    }
}
