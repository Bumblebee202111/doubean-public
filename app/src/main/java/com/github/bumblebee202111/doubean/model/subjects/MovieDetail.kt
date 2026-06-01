package com.github.bumblebee202111.doubean.model.subjects

import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class MovieDetail(
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
    val genres: List<String>,
    val actorNames: List<String>,
    val durations: List<String>,
    val trailers: List<MovieTrailer>,
    val countries: List<String>,
    val originalTitle: String?,
    val directorNames: List<String>,
) : SubjectDetail {
    override val type: SubjectType = SubjectType.MOVIE

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
            countries.toMetaInfoSegment()?.let { add(it) }
            genres.toMetaInfoSegment(n = 3, separator = " ")?.let { add(it) }
            pubdate.toMetaInfoSegment(postfix = "上映")?.let { add(it) }
            durations.toMetaInfoSegment(n = 2, prefix = "片长")?.let { add(it) }
        }.joinToString(" / ")
}