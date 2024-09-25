package com.github.bumblebee202111.doubean.feature.groups.common

import android.view.View
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.github.bumblebee202111.doubean.databinding.ListItemRecommendedGroupBinding
import com.github.bumblebee202111.doubean.model.RecommendedGroupItem
import com.github.bumblebee202111.doubean.ui.SmallGroupAvatar
import com.github.bumblebee202111.doubean.util.TopItemNoBackgroundUtil

fun LazyListScope.groupsOfTheDay(
    recommendedGroups: List<RecommendedGroupItem>,
    onGroupItemClick: (groupId: String) -> Unit,
) {

    item {
        Text(
            text = "Groups of the Day",
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.size(4.dp))


    }

    itemsIndexed(
        recommendedGroups,
        key = { _, recommendedGroup -> recommendedGroup.group.id },
        contentType = { _, _ -> "recommendedGroupItem" }) { index, group ->
        AndroidViewBinding({ inflater, root, attachToRoot ->
            ListItemRecommendedGroupBinding.inflate(
                inflater,
                root,
                attachToRoot
            )
        }) {
            val context = this.root.context
            val noColor = TopItemNoBackgroundUtil.getNoBackground(
                context,
                index,
                recommendedGroups.size
            )
            avatar.setContent {
                SmallGroupAvatar(
                    avatarUrl = group.group.avatarUrl
                )
            }
            this.noBackground = noColor
            this.recommendedGroup = group
            this.no.text = group.no.toString()
            this.clickListener = View.OnClickListener { onGroupItemClick(group.group.id) }
        }
    }

}
