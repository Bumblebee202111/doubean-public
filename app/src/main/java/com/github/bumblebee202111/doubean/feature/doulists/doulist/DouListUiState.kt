package com.github.bumblebee202111.doubean.feature.doulists.doulist

import com.github.bumblebee202111.doubean.shared.doulist.model.DouList
import com.github.bumblebee202111.doubean.shared.doulist.model.DouListPostItem
import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class DouListUiState(
    val douList: DouList? = null,
    val items: List<DouListPostItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: UiMessage? = null,
    val isOwner: Boolean = false,
)