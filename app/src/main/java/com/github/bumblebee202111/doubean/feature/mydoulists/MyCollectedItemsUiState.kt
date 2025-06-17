package com.github.bumblebee202111.doubean.feature.mydoulists

import com.github.bumblebee202111.doubean.model.common.DouListPostItem
import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class MyCollectedItemsUiState(
    val isLoading: Boolean = false,
    val items: List<DouListPostItem> = emptyList(),
    val errorMessage: UiMessage? = null
)
