package com.github.bumblebee202111.doubean.model.subjects

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

fun <T> List<T>.toMetaInfoSegment(
    n: Int = 1,
    separator: String = "",
    prefix: String = "",
    postfix: String = "",
): String? {
    return take(n).takeIf(List<T>::isNotEmpty)
        ?.joinToString(separator = separator, prefix = prefix, postfix = postfix)
}