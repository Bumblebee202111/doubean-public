package com.github.bumblebee202111.doubean.model.subjects

import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class TvDetail(
    override val id: String,
    override val rating: Rating,
    override val cardSubtitle: String,
    override val title: String,
    override val coverUrl: String,
    override val uri: String,
    override val intro: String,
    override val interest: SubjectInterest?,
    override val isReleased: Boolean,
    override val vendors: List<Vendor>,
    val pubdate: List<String>,
    val year: String,
    val languages: List<String>,
    val genres: List<String>,
    val actorNames: List<String>,
    val episodesCount: Int,
    val durations: List<String>,
    val trailers: List<MovieTrailer>,
    val countries: List<String>,
    val originalTitle: String?,
    val directorNames: List<String>,
) : SubjectDetail {
    override val type: SubjectType = SubjectType.TV

    override val displayTitle: UiMessage
        get() = originalTitle?.let { UiMessage.Direct(title) }
            ?: UiMessage.Resource(R.string.subject_title_year, listOf(title, year))

    override val displaySubtitle: UiMessage?
        get() = originalTitle?.let {
            UiMessage.Resource(
                R.string.subject_title_year,
                listOf(it, year)
            )
        }

    override val displayMetaInfo: String
        get() = buildList {
            countries.firstOrNull()?.let { add(it) }
            genres.take(3).joinToString(" ").takeIf { it.isNotEmpty() }?.let { add(it) }
            pubdate.firstOrNull()?.let { add("${it}首播") }
            if (episodesCount > 0) add("共${episodesCount}集")
            durations.firstOrNull()?.let { add("单集片长$it") }
        }.joinToString(" / ")
}