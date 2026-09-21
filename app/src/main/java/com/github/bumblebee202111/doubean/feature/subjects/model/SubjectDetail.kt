package com.github.bumblebee202111.doubean.feature.subjects.model

import com.github.bumblebee202111.doubean.shared.subject.model.MarkableSubject
import com.github.bumblebee202111.doubean.shared.subject.model.Rating
import com.github.bumblebee202111.doubean.shared.subject.model.SubjectInterest
import com.github.bumblebee202111.doubean.shared.subject.model.SubjectType
import com.github.bumblebee202111.doubean.ui.model.UiMessage

sealed interface SubjectDetail : MarkableSubject {
    override val id: String
    val rating: Rating
    val title: String
    val cardSubtitle: String
    val coverUrl: String
    val uri: String
    val intro: String
    val isReleased: Boolean
    val vendors: List<Vendor>

    override val type: SubjectType
    override val interest: SubjectInterest?

    
    val displayTitle: UiMessage
    val displaySubtitle: UiMessage?
    val displayMetaInfo: String
}