package com.github.bumblebee202111.doubean.feature.subjects.model

import com.github.bumblebee202111.doubean.model.BackgroundColorScheme
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithRank

data class SubjectCollectionItem(
    val id: String,
    val type: String,
    val name: String,
    val items: List<SubjectWithRank<*>>,
    val headerBgImage: String?,
    val colorScheme: BackgroundColorScheme,
)