package com.github.bumblebee202111.doubean.feature.subjects.model

import com.github.bumblebee202111.doubean.model.subjects.Rating
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class BookDetail(
    override val id: String,
    override val rating: Rating,
    override val title: String,
    override val cardSubtitle: String,
    override val coverUrl: String,
    override val uri: String,
    override val intro: String,
    override val interest: SubjectInterest?,
    override val isReleased: Boolean,
    override val vendors: List<Vendor> = emptyList(),
    val pubdate: List<String>,
    val author: List<String>,
    val subtitle: String?,
    val producers: List<String>,
    val press: List<String>,
    val pages: List<String>,
    val bookSeries: BookSeries?,
) : SubjectDetail {
    override val type: SubjectType = SubjectType.BOOK

    override val displayTitle: UiMessage
        get() = UiMessage.Direct(title)

    override val displaySubtitle: UiMessage?
        get() = subtitle?.let { UiMessage.Direct(it) }

    override val displayMetaInfo: String
        get() = buildList {
            author.firstOrNull()?.let { add(it + if (author.size > 1) " 等" else "") }
            press.firstOrNull()?.let { add(it) }
            producers.firstOrNull()?.let { add(it) }
            pubdate.firstOrNull()?.let { add("${it}出版") }
            pages.firstOrNull()?.let { add("${it}页") }
        }.joinToString(" / ")
}