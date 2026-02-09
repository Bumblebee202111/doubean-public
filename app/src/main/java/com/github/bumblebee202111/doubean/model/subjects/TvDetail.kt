package com.github.bumblebee202111.doubean.model.subjects

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
}
