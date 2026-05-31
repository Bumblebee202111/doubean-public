package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.github.bumblebee202111.doubean.model.subjects.RecommendSubject
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.util.OpenInUtils

@Composable
fun rememberRecommendSubjectClickHandler(
    onSubjectClick: (id: String, type: SubjectType) -> Unit,
): (RecommendSubject) -> Unit {
    val context = LocalContext.current
    return remember(context, onSubjectClick) {
        { recommendSubject ->
            if (recommendSubject.type == SubjectType.UNSUPPORTED) {
                OpenInUtils.openInDouban(context, recommendSubject.uri)
            } else {
                onSubjectClick(recommendSubject.id, recommendSubject.type)
            }
        }
    }
}