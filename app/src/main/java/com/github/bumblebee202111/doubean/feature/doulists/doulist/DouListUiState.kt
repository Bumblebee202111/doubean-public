package com.github.bumblebee202111.doubean.feature.doulists.doulist

import com.github.bumblebee202111.doubean.model.common.DouListPostItem
import com.github.bumblebee202111.doubean.model.doulists.DouList
import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class DouListUiState(
    val douList: DouList? = null,
    val items: List<DouListPostItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: UiMessage? = null,
    val isOwner: Boolean = false,
)