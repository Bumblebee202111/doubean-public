package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.fangorns.ReactionType

data class TopicReactionPartialEntity(
    val id: String,
    @ColumnInfo("reaction_type")
    val reactionType: ReactionType,
)
