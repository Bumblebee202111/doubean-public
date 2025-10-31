package com.github.bumblebee202111.doubean.feature.groups.topic

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.groups.TopicComment
import com.github.bumblebee202111.doubean.model.groups.TopicDetail
import com.github.bumblebee202111.doubean.util.ShareUtil

private fun createTopicCommentShareText(
    comment: TopicComment,
    topic: TopicDetail,
    context: Context,
): String {
    return buildString {
        topic.group?.let { group -> append(group.name) }
        topic.tag?.let { tag -> append("|" + tag.name) }
        append("@${comment.author.name}${context.getString(R.string.colon)}${comment.text}")
        comment.refComment?.let { repliedTo ->
            append("${context.getString(R.string.repliedTo)}@${repliedTo.author.name}： ${repliedTo.text}")
        }
        append("${context.getString(R.string.topic)}@${topic.author.name}${context.getString(R.string.colon)}\n${topic.title} ${topic.url}\n")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicCommentActionsBottomSheet(
    comment: TopicComment,
    topic: TopicDetail,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.share)) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = null
                )
            },
            modifier = Modifier.clickable {
                val shareText = createTopicCommentShareText(comment, topic, context)
                ShareUtil.share(context, shareText)
                onDismissRequest()
            }
        )
    }
}