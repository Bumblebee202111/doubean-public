package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.model.groups.GroupItemWithIntroInfo

fun LazyListScope.dayRankingItems(
    items: List<GroupItemWithIntroInfo>,
    onItemClick: (groupId: String) -> Unit,
) {
    itemsIndexed(
        items,
        key = { _, item -> item.id },
        contentType = { _, _ -> "day_ranking_item" }) { index, group ->
        DayRankingGroupItem(
            group = group,
            position = index + 1,
            total = items.size,
            onClick = {
                onItemClick(group.id)
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (index != items.size - 1) {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
