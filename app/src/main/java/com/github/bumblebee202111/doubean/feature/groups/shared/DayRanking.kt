package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.model.groups.GroupItemWithIntroInfo

fun LazyListScope.dayRanking(
    recommendedGroups: List<GroupItemWithIntroInfo>,
    onGroupItemClick: (groupId: String) -> Unit,
) {

    item {
        Text(
            text = "Day Ranking",
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.size(8.dp))
    }

    itemsIndexed(
        recommendedGroups,
        key = { _, recommendedGroup -> recommendedGroup.id },
        contentType = { _, _ -> "dayRankingGroupItem" }) { index, group ->
        DayRankingGroupItem(
            group = group,
            position = index + 1,
            total = recommendedGroups.size,
            onClick = {
                onGroupItemClick(group.id)
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (index != recommendedGroups.size - 1) {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

}
