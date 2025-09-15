package com.github.bumblebee202111.doubean.feature.subjects.tv

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.DouListRepository
import com.github.bumblebee202111.doubean.data.repository.ItemDouListRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCommonRepository
import com.github.bumblebee202111.doubean.data.repository.TvRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.common.CollectionHandler
import com.github.bumblebee202111.doubean.feature.subjects.tv.navigation.TvRoute
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.common.CollectType
import com.github.bumblebee202111.doubean.model.doulists.ItemDouList
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterest
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvViewModel @Inject constructor(
    private val tvRepository: TvRepository,
    private val userSubjectRepository: UserSubjectRepository,
    private val subjectCommonRepository: SubjectCommonRepository,
    private val authRepository: AuthRepository,
    itemDouListRepository: ItemDouListRepository,
    douListRepository: DouListRepository,
    savedStateHandle: SavedStateHandle,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {
    private val tvId = savedStateHandle.toRoute<TvRoute>().tvId

    private val _uiState = MutableStateFlow<TvUiState>(TvUiState.Loading)
    val uiState: StateFlow<TvUiState> = _uiState.asStateFlow()

    private var loadDataJob: Job? = null

    private val collectionHandler = CollectionHandler(
        scope = viewModelScope,
        itemDouListRepository = itemDouListRepository,
        douListRepository = douListRepository,
        snackbarManager = snackbarManager
    )

    val collectDialogUiState = collectionHandler.collectDialogUiState
    val showCreateDouListDialog = collectionHandler.showCreateDialogEvent

    init {
        viewModelScope.launch {
            authRepository.isLoggedIn()
                .distinctUntilChanged()
                .collect { isLoggedIn ->
                    triggerDataLoad(isLoggedIn)
                }
        }
    }

    private fun triggerDataLoad(isLoggedIn: Boolean) {
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch {
            _uiState.value = TvUiState.Loading

            val tvResultDeferred = async { tvRepository.getTv(tvId) }
            val interestsResultDeferred = async {
                userSubjectRepository.getSubjectDoneFollowingHotInterests(SubjectType.TV, tvId)
            }
            val photosResultDeferred = async { tvRepository.getPhotos(tvId) }
            val recommendationsDeferred = async {
                subjectCommonRepository.getSubjectRelatedItems(SubjectType.TV, tvId)
            }
            val reviewsResultDeferred = async {
                subjectCommonRepository.getSubjectReviews(
                    subjectType = SubjectType.TV,
                    subjectId = tvId
                )
            }

            val tvResult = tvResultDeferred.await()
            val interestResult = interestsResultDeferred.await()
            val photosResult = photosResultDeferred.await()
            val recommendationsResult = recommendationsDeferred.await()
            val reviewsResult = reviewsResultDeferred.await()

            val results =
                listOf(tvResult, interestResult, photosResult, recommendationsResult, reviewsResult)
            results.filterIsInstance<AppResult.Error>().forEach { errorResult ->
                snackbarManager.showMessage(errorResult.error.asUiMessage())
            }

            if (tvResult is AppResult.Success &&
                interestResult is AppResult.Success &&
                photosResult is AppResult.Success &&
                recommendationsResult is AppResult.Success &&
                reviewsResult is AppResult.Success
            ) {
                _uiState.value = TvUiState.Success(
                    tv = tvResult.data,
                    interests = interestResult.data,
                    photos = photosResult.data,
                    recommendations = recommendationsResult.data,
                    reviews = reviewsResult.data,
                    isLoggedIn = isLoggedIn
                )
            } else {
                _uiState.value = TvUiState.Error
            }
        }
    }

    suspend fun refreshData() {
        val currentLoginStatus = authRepository.isLoggedIn().first()
        triggerDataLoad(currentLoginStatus)
    }

    fun updateStatus(newStatus: SubjectInterestStatus) {
        val currentSuccessState = _uiState.value as? TvUiState.Success ?: return

        if (!currentSuccessState.isLoggedIn) {
            return
        }

        val originalTv = currentSuccessState.tv

        viewModelScope.launch {
            val result: AppResult<Any> = when (newStatus) {
                SubjectInterestStatus.MARK_STATUS_UNMARK ->
                    userSubjectRepository.unmarkSubject(originalTv.type, originalTv.id)

                else ->
                    userSubjectRepository.addSubjectToInterests(
                        type = originalTv.type,
                        id = originalTv.id,
                        newStatus = newStatus
                    )
            }

            when (result) {
                is AppResult.Success -> {
                    val confirmedInterest: SubjectInterest? = when (val data = result.data) {
                        is SubjectInterest -> data
                        is SubjectWithInterest<*> -> data.interest
                        else -> {

                            originalTv.interest
                        }
                    }
                    val confirmedTv = originalTv.copy(interest = confirmedInterest)

                    (_uiState.value as? TvUiState.Success)?.takeIf { it.tv.id == originalTv.id }
                        ?.let { latestSuccessState ->
                            _uiState.value = latestSuccessState.copy(tv = confirmedTv)
                        }
                }

                is AppResult.Error -> {
                    snackbarManager.showMessage(result.error.asUiMessage())
                }
            }
        }
    }

    fun collect() {
        collectionHandler.showCollectDialog(CollectType.TV, tvId)
    }

    fun dismissCollectDialog() = collectionHandler.dismissCollectDialog()

    fun showCreateDialog() = collectionHandler.showCreateDialog()

    fun dismissCreateDialog() = collectionHandler.dismissCreateDialog()

    fun createAndCollect(title: String) {
        viewModelScope.launch {
            collectionHandler.createAndCollect(
                title = title,
                type = CollectType.TV,
                id = tvId
            )
        }
    }

    fun toggleCollection(douList: ItemDouList) {
        viewModelScope.launch {
            collectionHandler.toggleCollection(
                type = CollectType.TV,
                id = tvId,
                douList = douList
            )
        }
    }
}
