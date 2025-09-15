package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.model.subjects.SearchResultSubjectItem
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectItemColumn
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectItemImage

@Composable
fun RowScope.SubjectItemBasicContent(subject: SearchResultSubjectItem, showType: Boolean = false) {
    SubjectItemImage(url = subject.coverUrl)
    Spacer(modifier = Modifier.size(8.dp))
    SubjectItemColumn(subject = subject, showType = showType)
}

@Composable
fun RowScope.SubjectItemColumn(
    subject: SearchResultSubjectItem,
    showType: Boolean = false,
) {
    with(subject) {
        SubjectItemColumn(
            title = title,
            rating = rating,
            cardSubtitle = cardSubtitle,
            type = subject.type,
            subtitle = abstract,
            showType = showType
        )
    }
}
