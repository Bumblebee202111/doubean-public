package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.subjects.Subject
import com.github.bumblebee202111.doubean.model.subjects.SubjectCollectionItem
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectItem
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectItemBasicContent
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectItemRank

@Composable
fun RankLists(
    rankLists: List<SubjectCollectionItem>,
    onRankListClick: (collectionId: String) -> Unit,
    onSubjectClick: (subject: Subject) -> Unit,
) {
        Text(
            text = stringResource(id = R.string.title_subject_rank_lists),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.size(4.dp))
        LazyRow(
            modifier = Modifier
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = rankLists, key = { it.id }) { rankList ->
                RankListItem(
                    rankList = rankList,
                    onClick = { onRankListClick(rankList.id) },
                    onSubjectClick = onSubjectClick
                )
            }
        }
}

@Composable
private fun RankListItem(
    rankList: SubjectCollectionItem,
    onClick: () -> Unit,
    onSubjectClick: (subject: Subject) -> Unit,
) {
    Card(onClick = onClick) {
        Box(Modifier.width(290.dp)) {
            AsyncImage(
                model = rankList.headerBgImage,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter,
                contentDescription = null
            )
            Column {
                CompositionLocalProvider(
                    value = LocalContentColor provides Color.White
                ) {
                    val primaryColorDark =
                        Color(rankList.colorScheme.primaryColorDark.toColorInt())
                    Text(
                        text = rankList.name,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    1f to primaryColorDark
                                )
                            )
                            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Column(
                        modifier = Modifier
                            .background(primaryColorDark)
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rankList.items.forEach { subject ->
                            SubjectItem(
                                basicContent = {
                                    SubjectItemBasicContent(subject = subject.subject)
                                },
                                onClick = {
                                    onSubjectClick(subject.subject)
                                },
                                rankContent = {
                                    SubjectItemRank(
                                        rankValue = subject.rankValue,
                                        listSize = rankList.items.size
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}