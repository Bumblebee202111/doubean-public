package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.PhotoList
import com.github.bumblebee202111.doubean.model.subjects.CreditList
import com.github.bumblebee202111.doubean.model.subjects.RecommendSubject
import com.github.bumblebee202111.doubean.model.subjects.SubjectDetail
import com.github.bumblebee202111.doubean.model.subjects.SubjectModule
import com.github.bumblebee202111.doubean.model.subjects.SubjectReviewList
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.network.api.SubjectApiService
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectModules
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectReviewList
import com.github.bumblebee202111.doubean.network.model.subject.NetworkRecommend
import com.github.bumblebee202111.doubean.network.model.subject.toBookDetail
import com.github.bumblebee202111.doubean.network.model.subject.toCreditList
import com.github.bumblebee202111.doubean.network.model.subject.toMovieDetail
import com.github.bumblebee202111.doubean.network.model.subject.toMusicDetail
import com.github.bumblebee202111.doubean.network.model.subject.toRecommendSubject
import com.github.bumblebee202111.doubean.network.model.subject.toTvDetail
import com.github.bumblebee202111.doubean.network.model.toNetworkSubjectType
import com.github.bumblebee202111.doubean.network.model.toPhotoList
import com.github.bumblebee202111.doubean.network.model.toSubjectModules
import com.github.bumblebee202111.doubean.network.model.toSubjectReviewList
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
}