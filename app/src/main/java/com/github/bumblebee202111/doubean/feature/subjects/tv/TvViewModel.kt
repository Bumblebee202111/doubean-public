package com.github.bumblebee202111.doubean.feature.subjects.tv

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.coroutines.combine
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCommonRepository
import com.github.bumblebee202111.doubean.data.repository.TvRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.tv.navigation.TvRoute
import com.github.bumblebee202111.doubean.model.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.TvDetail
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvViewModel @Inject constructor(
    private val tvRepository: TvRepository,
    private val userSubjectRepository: UserSubjectRepository,
    private val subjectCommonRepository: SubjectCommonRepository,
    authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val tvId = savedStateHandle.toRoute<TvRoute>().tvId
    private val tv = MutableStateFlow<TvDetail?>(null)
    private val tvResult = flow {
        emit(tvRepository.getTv(tvId))
    }.onEach {
        tv.value = it.getOrNull()
    }
    private val interestList = flow {
        emit(
            userSubjectRepository.getSubjectDoneFollowingHotInterests(SubjectType.TV, tvId)
        )
    }
    private val photos = flow {
        emit(
            tvRepository.getPhotos(tvId)
        )
    }
    private val reviews = flow {
        emit(
            subjectCommonRepository.getSubjectReviews(
                subjectType = SubjectType.TV,
                subjectId = tvId
            )
        )
    }
    private val isLoggedIn = authRepository.isLoggedIn()
    val tvUiState = combine(
        tv,
        tvResult,
        interestList,
        photos,
        reviews,
        isLoggedIn
    ) { tv, tvResult, interestList, photos, reviews, isLoggedIn ->
        if (tvResult.isSuccess) {
            TvUiState.Success(
                tv = tv!!,
                interests = interestList.getOrThrow(),
                photos = photos.getOrThrow(),
                reviews = reviews.getOrThrow(),
                isLoggedIn = isLoggedIn
            )
        } else {
            TvUiState.Error
        }
    }.stateInUi(TvUiState.Loading)

    fun onUpdateStatus(
        status: SubjectInterestStatus,
    ) {
        val oldTv = tv.value ?: return
        when (status) {
            SubjectInterestStatus.MARK_STATUS_UNMARK -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.unmarkSubject(oldTv.type, oldTv.id)
                    if (result.isSuccess) {
                        tv.value = tv.value?.copy(interest = result.getOrThrow())
                    }
                }
            }

            else -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.addSubjectToInterests(
                        type = oldTv.type, id = oldTv.id,
                        newStatus = status
                    )
                    if (result.isSuccess) {
                        tv.update {
                            it?.copy(interest = result.getOrThrow().interest)
                        }
                    }
                }
            }
        }
    }

}