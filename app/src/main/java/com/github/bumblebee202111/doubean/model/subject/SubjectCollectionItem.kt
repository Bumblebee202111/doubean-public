package com.github.bumblebee202111.doubean.model.subject

import com.github.bumblebee202111.doubean.model.BackgroundColorScheme

data class SubjectCollectionItem(
    val id: String,
    val type: String,
    val name: String,
    val items: List<SubjectWithRank<*>>,
    val headerBgImage: String?,
    val colorScheme: BackgroundColorScheme,
)