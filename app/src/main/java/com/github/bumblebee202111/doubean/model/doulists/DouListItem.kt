package com.github.bumblebee202111.doubean.model.doulists

import com.github.bumblebee202111.doubean.model.subjects.Rating
import java.time.LocalDateTime

data class DouListItem(
    val id: String,
    val type: String,
    val createTime: LocalDateTime,
    val uri: String,
    val targetId: String,
    val title: String,
    val subtitle: String,
    val coverUrl: String,
    val comment: String,
    val rating: Rating? = null,
)