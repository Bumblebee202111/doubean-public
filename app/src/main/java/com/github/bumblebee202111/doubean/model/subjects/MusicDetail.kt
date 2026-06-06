package com.github.bumblebee202111.doubean.model.subjects

import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class MusicDetail(
    override val id: String,
    override val rating: Rating,
    override val cardSubtitle: String,
    override val title: String,
    override val coverUrl: String,
    override val uri: String,
    override val intro: String,
    override val interest: SubjectInterest?,
    override val isReleased: Boolean,
    override val vendors: List<Vendor> = emptyList(),
    val pubdate: List<String>,
    val genres: List<String>,
    val singer: List<String>,
    val tracks: List<String>,
) : SubjectDetail {
    override val type: SubjectType = SubjectType.MUSIC

    override val displayTitle: UiMessage
        get() = UiMessage.Direct(title)

    override val displaySubtitle: UiMessage?
        get() = null

    override val displayMetaInfo: String
        get() = buildList {
            if (singer.isNotEmpty()) {
                val singerStr = singer.take(3).joinToString(" ")
                add(if (singer.size > 3) "$singerStr 等" else singerStr)
            }
            genres.take(3).joinToString(" ").takeIf { it.isNotEmpty() }?.let { add(it) }
            pubdate.firstOrNull()?.let { add("${it}发行") }
        }.joinToString(" / ")
}