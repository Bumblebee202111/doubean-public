package com.github.bumblebee202111.doubean.feature.subjects.mapper

import com.github.bumblebee202111.doubean.feature.subjects.model.BookDetail
import com.github.bumblebee202111.doubean.feature.subjects.model.MovieDetail
import com.github.bumblebee202111.doubean.feature.subjects.model.MusicDetail
import com.github.bumblebee202111.doubean.feature.subjects.model.TvDetail
import com.github.bumblebee202111.doubean.network.model.subject.NetworkBookDetail
import com.github.bumblebee202111.doubean.network.model.subject.NetworkMovieDetail
import com.github.bumblebee202111.doubean.network.model.subject.NetworkMovieTrailer
import com.github.bumblebee202111.doubean.network.model.subject.NetworkMusicDetail
import com.github.bumblebee202111.doubean.network.model.subject.NetworkSimpleCelebrity
import com.github.bumblebee202111.doubean.network.model.subject.NetworkSong
import com.github.bumblebee202111.doubean.network.model.subject.NetworkTvDetail
import com.github.bumblebee202111.doubean.network.model.subject.NetworkVendor
import com.github.bumblebee202111.doubean.network.model.subject.toMovieTrailer
import com.github.bumblebee202111.doubean.network.model.subject.toSubjectInterest
import com.github.bumblebee202111.doubean.network.model.toRating


fun NetworkMovieDetail.toMovieDetail() = MovieDetail(
    id = id,
    rating = Pair(rating, nullRatingReason).toRating(),
    cardSubtitle = cardSubtitle,
    title = title,
    coverUrl = coverUrl,
    uri = uri,
    intro = intro,
    interest = interest?.toSubjectInterest(),
    isReleased = isReleased,
    vendors = vendors.map(NetworkVendor::toVendor),
    pubdate = pubdate,
    year = year,
    genres = genres,
    actorNames = actors.map(NetworkSimpleCelebrity::name),
    durations = durations,
    trailers = trailers.map(NetworkMovieTrailer::toMovieTrailer),
    countries = countries,
    originalTitle = originalTitle.takeIf(String::isNotEmpty),
    directorNames = directors.map(NetworkSimpleCelebrity::name)
)

fun NetworkTvDetail.toTvDetail() = TvDetail(
    id = id,
    rating = Pair(rating, nullRatingReason).toRating(),
    cardSubtitle = cardSubtitle,
    title = title,
    coverUrl = coverUrl,
    uri = uri,
    intro = intro,
    interest = interest?.toSubjectInterest(),
    isReleased = isReleased,
    vendors = vendors.map(NetworkVendor::toVendor),
    pubdate = pubdate,
    year = year,
    languages = languages,
    genres = genres,
    actorNames = actors.map(NetworkSimpleCelebrity::name),
    episodesCount = episodesCount,
    durations = durations,
    trailers = trailers.map(NetworkMovieTrailer::toMovieTrailer),
    countries = countries,
    originalTitle = originalTitle.takeIf(String::isNotEmpty),
    directorNames = directors.map(NetworkSimpleCelebrity::name)
)


fun NetworkBookDetail.toBookDetail() = BookDetail(
    id = id,
    rating = Pair(rating, nullRatingReason).toRating(),
    cardSubtitle = cardSubtitle,
    title = title,
    coverUrl = coverUrl,
    uri = uri,
    intro = intro,
    interest = interest?.toSubjectInterest(),
    isReleased = isReleased,
    pubdate = pubdate,
    author = author,
    subtitle = bookSubtitle.takeIf(String::isNotEmpty),
    producers = producers,
    press = press,
    pages = pages,
)

fun NetworkMusicDetail.toMusicDetail() = MusicDetail(
    id = id,
    rating = Pair(rating, nullRatingReason).toRating(),
    cardSubtitle = cardSubtitle,
    title = title,
    coverUrl = coverUrl,
    uri = uri,
    intro = intro,
    interest = interest?.toSubjectInterest(),
    isReleased = isReleased,
    pubdate = pubdate,
    genres = genres,
    singer = singer.map(NetworkSimpleCelebrity::name),
    tracks = songs.map(NetworkSong::title),
)
