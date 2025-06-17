package com.github.bumblebee202111.doubean.feature.doulists.common

import com.github.bumblebee202111.doubean.model.common.DouListPostItem
import com.github.bumblebee202111.doubean.model.common.FeedItem
import com.github.bumblebee202111.doubean.model.common.SubjectFeedContent
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest

object DouListStateHelper {
    fun getUpdatedListWithNewInterest(
        currentItems: List<DouListPostItem>,
        updatedSubjectWithInterest: SubjectWithInterest<*>,
    ): List<DouListPostItem> {
        return currentItems.map { douListPostItem ->
            val feedItem = douListPostItem.feedItem
            val feedContent = feedItem.content
            if (feedContent is SubjectFeedContent && feedContent.subject.subject.id == updatedSubjectWithInterest.subject.id) {
                @Suppress("UNCHECKED_CAST")
                douListPostItem.copy(
                    feedItem = (feedItem as FeedItem<SubjectFeedContent>).copy(
                        content = feedContent.copy(subject = updatedSubjectWithInterest)
                    )
                )
            } else {
                douListPostItem
            }
        }
    }
}