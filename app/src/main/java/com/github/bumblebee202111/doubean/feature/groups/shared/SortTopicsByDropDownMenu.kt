package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.bumblebee202111.doubean.feature.subjects.shared.SortTopicsByOption
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.ui.component.SortByDropDownMenu

@Composable
fun SortTopicsByDropDownMenu(
    initialSortBy: TopicSortBy,
    onSortBySelected: (sortBy: TopicSortBy) -> Unit,
    enabled: Boolean = true,
) {

    SortByDropDownMenu(
        options = SortTopicsByOption.entries,
        initialSelectedValue = initialSortBy,
        onOptionSelected = onSortBySelected,
        optionText = { stringResource(it.textRes) },
        optionToValue = { it.sortBy },
        enabled = enabled
    )
}

