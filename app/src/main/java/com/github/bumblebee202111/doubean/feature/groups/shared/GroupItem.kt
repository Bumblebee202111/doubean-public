package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.groups.GroupSearchResultGroupItem
import com.github.bumblebee202111.doubean.model.groups.RecommendedGroupItem

@Composable
fun SearchResultGroupItem(group: GroupSearchResultGroupItem?, onClick: () -> Unit) {
    Row(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        GroupItemContent(
            avatarUrl = group?.avatarUrl,
            name = group?.name,
            memberName = group?.memberName,
            memberCount = group?.memberCount,
            descAbstract = group?.descAbstract
        )
    }
}

@Composable
fun DayRankingGroupItem(
    group: RecommendedGroupItem,
    position: Int,
    total: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val rankBackgroundColor = rankBackgroundColor(currentPosition = position, total = total)
        Box(
            Modifier
                .size(24.dp)
                .background(color = rankBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = position.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.margin_normal)))
        GroupItemContent(
            avatarUrl = group.group.avatarUrl,
            name = group.group.name,
            memberName = group.group.memberName,
            memberCount = group.group.memberCount,
            descAbstract = group.group.descAbstract
        )
    }
}

@Composable
fun GroupItemContent(
    avatarUrl: String?,
    name: String?,
    memberName: String?,
    memberCount: Int?,
    descAbstract: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        LargeGroupAvatar(avatarUrl = avatarUrl)
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.margin_small)))
        Column {
            Text(
                text = name ?: "",
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_extra_small)))
            Text(
                text = (memberCount?.toString() ?: "") + (memberName ?: ""),
                fontWeight = FontWeight.Light,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_extra_small)))
            Text(
                text = descAbstract ?: "",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun rankBackgroundColor(
    currentPosition: Int,
    total: Int,
): Color {
    val rankStartColor = Color(0xFFFF0000)
    val rankEndColor = Color(0xFFFF9900)
    val ratio = (currentPosition - 1) / (total - 1f)
    return lerp(rankStartColor, rankEndColor, ratio)
}