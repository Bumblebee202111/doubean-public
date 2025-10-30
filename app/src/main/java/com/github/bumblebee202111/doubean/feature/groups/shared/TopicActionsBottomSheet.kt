package com.github.bumblebee202111.doubean.feature.groups.shared

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
import com.github.bumblebee202111.doubean.model.groups.AbstractTopicItem
import com.github.bumblebee202111.doubean.model.groups.SimpleGroup
import com.github.bumblebee202111.doubean.util.ShareUtil

private fun createTopicShareText(
    topic: AbstractTopicItem,
    group: SimpleGroup?,
    context: Context,
): CharSequence {
    return buildString {
        group?.let { append(it.name) }
        if (group != null && topic.tag != null) {
            append("|")
        }
        topic.tag?.let { tag ->
            append(tag.name)
        }
        append("@${topic.author.name}${context.getString(R.string.colon)}${topic.title} ${topic.url}")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicActionsBottomSheet(
    topic: AbstractTopicItem,
    group: SimpleGroup?,
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
                val shareText = createTopicShareText(topic, group, context)
                ShareUtil.share(context, shareText)
                onDismissRequest()
            }
        )
    }
}