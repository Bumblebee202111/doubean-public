package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.model.subjects.SubjectReviewList
import com.github.bumblebee202111.doubean.model.subjects.SubjectType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScaffold(
    reviewsSheetContent: @Composable ColumnScope.() -> Unit,
    topBar: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    BottomSheetScaffold(
        sheetContent = reviewsSheetContent,
        sheetPeekHeight = 128.dp,
        topBar = topBar,
        content = content
    )
}

@Composable
fun SubjectReviewsSheetContent(
    subjectType: SubjectType,
    reviews: SubjectReviewList,
    onUserClick: (userId: String) -> Unit,
) {
    SubjectInfoReviewsModuleItemContent(
        subjectType = subjectType,
        reviews = reviews,
        onUserClick = onUserClick
    )
}
