package com.github.bumblebee202111.doubean.feature.groups.common

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.ListItemPostNotificationBinding
import com.github.bumblebee202111.doubean.model.TopicItemWithGroup
import com.github.bumblebee202111.doubean.ui.common.UserProfileImage

@Composable
fun TopicItemWithGroupAndroidView(
    topicItemWithGroup: TopicItemWithGroup?,
    navigateToTopic: (id: String) -> Unit,
) {
    AndroidViewBinding(
        factory = ListItemPostNotificationBinding::inflate,
        onReset = {}) {
        post = topicItemWithGroup
        //TODO Do not use Circle for group avatars
        groupAvatar.setContent {
            UserProfileImage(
                url = topicItemWithGroup?.group?.avatarUrl,
                size = dimensionResource(id = R.dimen.icon_size_extra_small)
            )
        }
        cover.setContent {
            topicItemWithGroup?.coverUrl?.let {
                AsyncImage(
                    model = it, contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_size_normal))),
                    contentScale = ContentScale.Crop
                )
            }

        }
        card.setOnClickListener {
            topicItemWithGroup?.let { navigateToTopic(it.id) }
        }
    }
}