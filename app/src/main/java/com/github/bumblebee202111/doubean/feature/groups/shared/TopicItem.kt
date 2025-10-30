package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.groups.AbstractTopicItem
import com.github.bumblebee202111.doubean.model.groups.SimpleGroup
import com.github.bumblebee202111.doubean.model.groups.TopicItemWithGroup
import com.github.bumblebee202111.doubean.ui.component.DateTimeText
import com.github.bumblebee202111.doubean.ui.component.ListItemCount
import com.github.bumblebee202111.doubean.ui.component.UserProfileImage
import com.github.bumblebee202111.doubean.util.DateTimeStyle
import com.github.bumblebee202111.doubean.util.buildGroupTopicAndTagText
import com.github.bumblebee202111.doubean.util.toRelativeString

@Composable
fun TopicItem(
    topicItemWithGroup: TopicItemWithGroup?,
    displayMode: TopicItemDisplayMode,
    onTopicClick: (id: String) -> Unit,
    onAuthorClick: (id: String) -> Unit = {},
    onGroupClick: (id: String) -> Unit = {},
) {
    TopicItem(
        topic = topicItemWithGroup,
        group = topicItemWithGroup?.group,
        displayMode = displayMode,
        onTopicClick = onTopicClick,
        onAuthorClick = onAuthorClick,
        onGroupClick = onGroupClick
    )
}

@Composable
fun TopicItem(
    topic: AbstractTopicItem?,
    group: SimpleGroup?,
    displayMode: TopicItemDisplayMode,
    onTopicClick: (id: String) -> Unit,
    onAuthorClick: (id: String) -> Unit = {},
    onGroupClick: (id: String) -> Unit = {},
) {
    var showActionsBottomSheet by remember { mutableStateOf(false) }

    if (showActionsBottomSheet && topic != null) {
        TopicActionsBottomSheet(
            topic = topic,
            group = group,
            onDismissRequest = { showActionsBottomSheet = false }
        )
    }

    if (topic != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTopicClick(topic.id) }
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (displayMode) {
                        TopicItemDisplayMode.SHOW_AUTHOR -> {
                            UserProfileImage(
                                url = topic.author.avatar,
                                size = dimensionResource(id = R.dimen.icon_size_extra_small),
                                onClick = {
                                    onAuthorClick(topic.author.id)
                                }
                            )
                        }

                        TopicItemDisplayMode.SHOW_GROUP -> {
                            SmallGroupAvatar(avatarUrl = group?.avatar)
                        }
                    }

                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.margin_small)))

                    Text(
                        text = when (displayMode) {
                            TopicItemDisplayMode.SHOW_AUTHOR -> topic.author.name
                            TopicItemDisplayMode.SHOW_GROUP -> group?.name
                        } ?: "",
                        modifier = Modifier
                            .weight(weight = 1f, fill = false)
                            .clickable {
                                when (displayMode) {
                                    TopicItemDisplayMode.SHOW_AUTHOR -> onAuthorClick(topic.author.id)
                                    TopicItemDisplayMode.SHOW_GROUP -> if (group != null) {
                                        onGroupClick(group.id)
                                    }
                                }

                            },
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = stringResource(id = R.string.middle_dot),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    DateTimeText(
                        text = topic.createTime.toRelativeString(style = DateTimeStyle.ABBREVIATED)
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_small)))
                val topicAndTagText = topic.tag?.let { tag ->
                    buildGroupTopicAndTagText(tag.name, topic.title)
                } ?: topic.title
                Text(text = topicAndTagText, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_small)))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ListItemCount(
                        iconVector = Icons.AutoMirrored.Outlined.Comment,
                        count = topic.commentsCount
                    )
                    if (topic.commentsCount != 0 || topic.createTime != topic.updateTime) {
                        Text(
                            text = stringResource(id = R.string.middle_dot),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        DateTimeText(
                            text = topic.updateTime.toRelativeString(style = DateTimeStyle.ABBREVIATED)
                        )
                    }
                }

            }

            IconButton(
                onClick = { showActionsBottomSheet = true },
                modifier = Modifier.padding(all = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.more)
                )
            }

            val coverUrl = topic.coverUrl
            if (coverUrl != null) {
                AsyncImage(
                    model = coverUrl, contentDescription = null,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .size(80.dp)
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_size_normal))),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                Spacer(modifier = Modifier.width(2.dp))
            }
        }
    }
}


enum class TopicItemDisplayMode {
    SHOW_AUTHOR, SHOW_GROUP
}