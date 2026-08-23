package com.github.bumblebee202111.doubean.feature.subjects.data

import com.github.bumblebee202111.doubean.feature.subjects.mapper.toBookDetail
import com.github.bumblebee202111.doubean.feature.subjects.mapper.toCreditList
import com.github.bumblebee202111.doubean.feature.subjects.mapper.toMovieDetail
import com.github.bumblebee202111.doubean.feature.subjects.mapper.toMusicDetail
import com.github.bumblebee202111.doubean.feature.subjects.mapper.toRecommendSubject
import com.github.bumblebee202111.doubean.feature.subjects.mapper.toSubjectReviewList
import com.github.bumblebee202111.doubean.feature.subjects.mapper.toTvDetail
import com.github.bumblebee202111.doubean.feature.subjects.model.BookVersions
import com.github.bumblebee202111.doubean.feature.subjects.model.CreditList
import com.github.bumblebee202111.doubean.feature.subjects.model.RecommendSubject
import com.github.bumblebee202111.doubean.feature.subjects.model.SubjectDetail
import com.github.bumblebee202111.doubean.feature.subjects.model.SubjectReviewList
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.PhotoList
import com.github.bumblebee202111.doubean.model.subjects.SubjectModule
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.network.api.SubjectApiService
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectModules
import com.github.bumblebee202111.doubean.network.model.fangorns.toPhotoList
import com.github.bumblebee202111.doubean.network.model.subject.NetworkRecommend
import com.github.bumblebee202111.doubean.network.model.subject.NetworkSubjectReviewList
import com.github.bumblebee202111.doubean.network.model.toBook
import com.github.bumblebee202111.doubean.network.model.toNetworkSubjectType
import com.github.bumblebee202111.doubean.network.model.toSubjectModules
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectCommonRepository @Inject constructor(private val service: SubjectApiService) {

    suspend fun getSubjectDetail(
        subjectType: SubjectType,
        subjectId: String,
    ): AppResult<SubjectDetail> {
        return makeApiCall(
            apiCall = {
                when (subjectType) {
                    SubjectType.MOVIE -> service.getMovie(subjectId).toMovieDetail()
                    SubjectType.TV -> service.getTv(subjectId).toTvDetail()
                    SubjectType.BOOK -> service.getBook(subjectId).toBookDetail()
                    SubjectType.MUSIC -> service.getMusic(subjectId).toMusicDetail()
                    else -> throw IllegalArgumentException("Unsupported subject type")
                }
            },
            mapSuccess = { it }
        )
    }

    suspend fun getSubjectPhotos(
        subjectType: SubjectType,
        subjectId: String,
    ): AppResult<PhotoList> {
        return makeApiCall(
            apiCall = {
                when (subjectType) {
                    SubjectType.MOVIE -> service.getMoviePhotos(subjectId).toPhotoList()
                    SubjectType.TV -> service.getTvPhotos(subjectId).toPhotoList()
                    else -> throw IllegalArgumentException("Photos not supported for $subjectType")
                }
            },
            mapSuccess = { it }
        )
    }

    suspend fun getSubjectCreditList(
        subjectType: SubjectType,
        subjectId: String,
    ): AppResult<CreditList> {
        return makeApiCall(
            apiCall = {
                service.getSubjectCredits(
                    subjectType = subjectType.toNetworkSubjectType().value,
                    subjectId = subjectId
                )
            },
            mapSuccess = { networkCreditList ->
                networkCreditList.toCreditList()
            }
        )
    }

    suspend fun getSubjectRelatedItems(
        subjectType: SubjectType,
        subjectId: String,
    ): AppResult<List<RecommendSubject>> {
        return makeApiCall(
            apiCall = {
                service.getSubjectRelatedItems(
                    subjectType = subjectType.toNetworkSubjectType().value,
                    subjectId = subjectId
                )
            },
            mapSuccess = { networkRecommendations ->
                networkRecommendations.subjects.mapNotNull { recommend ->
                    when (recommend) {
                        is NetworkRecommend.Subject -> recommend.subject.toRecommendSubject()
                        else -> null
                    }
                }
            }
        )
    }

    suspend fun getSubjectReviews(
        subjectType: SubjectType,
        subjectId: String,
    ): AppResult<SubjectReviewList> =
        makeApiCall(
            apiCall = {
                service.getSubjectReviews(
                    subjectType = subjectType.toNetworkSubjectType().value,
                    subjectId = subjectId
                )
            },
            mapSuccess = NetworkSubjectReviewList::toSubjectReviewList
        )

    suspend fun getSubjectModules(subjectType: SubjectType): AppResult<List<SubjectModule>> =
        makeApiCall(
            apiCall = {
                service.getSubjectModules(subjectType = subjectType.toNetworkSubjectType().value)
            },
            mapSuccess = NetworkSubjectModules::toSubjectModules
        )

    suspend fun getBookVersions(subjectId: String): AppResult<BookVersions> =
        makeApiCall(
            apiCall = { service.getBookVersions(subjectId) },
            mapSuccess = { response ->
                BookVersions(
                    total = response.total,
                    versions = response.books.map { it.toBook() }
                )
            }
        )
}