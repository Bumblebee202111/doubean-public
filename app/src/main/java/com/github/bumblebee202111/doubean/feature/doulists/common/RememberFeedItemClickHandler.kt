package com.github.bumblebee202111.doubean.feature.doulists.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.github.bumblebee202111.doubean.model.common.FeedItem
import com.github.bumblebee202111.doubean.model.common.SubjectFeedContent
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.util.OpenInUtils

@Composable
fun rememberFeedItemClickHandler(
    onOpenDeepLinkUrl: (String) -> Boolean,
    onSubjectClick: (id: String, type: SubjectType) -> Unit,
): (FeedItem<*>) -> Unit {
    val context = LocalContext.current
    return remember(context, onOpenDeepLinkUrl, onSubjectClick) {
        { feedItem ->
            
            var handled = onOpenDeepLinkUrl(feedItem.uri)

            
            if (!handled) {
                val content = feedItem.content
                if (content is SubjectFeedContent && content.subject.type != SubjectType.UNSUPPORTED) {
                    onSubjectClick(content.subject.subject.id, content.subject.type)
                    handled = true
                }
            }

            
            if (!handled) {
                val openedInDoubanApp = OpenInUtils.openInDouban(context, feedItem.uri).isSuccess
                if (!openedInDoubanApp && feedItem.url.isNotBlank()) {
                    OpenInUtils.openInBrowser(context, feedItem.url)
                }
            }
        }
    }
}