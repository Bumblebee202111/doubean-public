package com.github.bumblebee202111.doubean.feature.subjects.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.DouListRepository
import com.github.bumblebee202111.doubean.data.repository.ItemDouListRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCommonRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.common.CollectionHandler
import com.github.bumblebee202111.doubean.feature.subjects.common.InterestSortType
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.common.CollectType
import com.github.bumblebee202111.doubean.model.doulists.ItemDouList
import com.github.bumblebee202111.doubean.model.subjects.BookDetail
import com.github.bumblebee202111.doubean.model.subjects.MovieDetail
import com.github.bumblebee202111.doubean.model.subjects.MusicDetail
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterest
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestWithUserList
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest
import com.github.bumblebee202111.doubean.model.subjects.TvDetail
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = SubjectViewModel.Factory::class)
class SubjectViewModel @AssistedInject constructor(
    private val subjectCommonRepository: SubjectCommonRepository,
    private val userSubjectRepository: UserSubjectRepository,
    private val authRepository: AuthRepository,
    itemDouListRepository: ItemDouListRepository,
    douListRepository: DouListRepository,
    private val snackbarManager: SnackbarManager,
    @Assisted val subjectId: String,
    @Assisted val subjectType: SubjectType,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubjectUiState>(SubjectUiState.Loading)
    val uiState: StateFlow<SubjectUiState> = _uiState.asStateFlow()

    private var loadDataJob: Job? = null

    private val collectionHandler = CollectionHandler(
        scope = viewModelScope,
        itemDouListRepository = itemDouListRepository,
        douListRepository = douListRepository,
        snackbarManager = snackbarManager
    )

    val collectDialogUiState = collectionHandler.collectDialogUiState
    val showCreateDouListDialog = collectionHandler.showCreateDialogEvent

    private var currentInterestSortType = InterestSortType.DEFAULT

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
            _uiState.value = SubjectUiState.Loading

            val detailResultDeferred =
                async { subjectCommonRepository.getSubjectDetail(subjectType, subjectId) }
            val recommendationsDeferred =
                async { subjectCommonRepository.getSubjectRelatedItems(subjectType, subjectId) }
            val reviewsResultDeferred =
                async { subjectCommonRepository.getSubjectReviews(subjectType, subjectId) }

            val photosResultDeferred =
                if (subjectType == SubjectType.MOVIE || subjectType == SubjectType.TV) {
                    async { subjectCommonRepository.getSubjectPhotos(subjectType, subjectId) }
                } else null

            val creditListResultDeferred =
                if (subjectType == SubjectType.MOVIE || subjectType == SubjectType.TV) {
                    async { subjectCommonRepository.getSubjectCreditList(subjectType, subjectId) }
                } else null

            val detailResult = detailResultDeferred.await()
            if (detailResult is AppResult.Error) {
                val uiMessage = detailResult.error.asUiMessage()
                snackbarManager.showMessage(uiMessage)
                _uiState.value = SubjectUiState.Error(uiMessage)
                return@launch
            }

            val subject = (detailResult as AppResult.Success).data
            val interestStatus =
                if (subject.isReleased) SubjectInterestStatus.MARK_STATUS_DONE else SubjectInterestStatus.MARK_STATUS_MARK

            val interestsResultDeferred = async {
                fetchInterests(
                    subjectType,
                    subjectId,
                    currentInterestSortType,
                    interestStatus
                )
            }

            val interestResult = interestsResultDeferred.await()
            val recommendationsResult = recommendationsDeferred.await()
            val reviewsResult = reviewsResultDeferred.await()
            val photosResult = photosResultDeferred?.await()
            val creditListResult = creditListResultDeferred?.await()

            val results = listOfNotNull(
                interestResult, recommendationsResult, reviewsResult, photosResult, creditListResult
            )

            val firstError = results.filterIsInstance<AppResult.Error>().firstOrNull()

            if (firstError != null) {
                val uiMessage = firstError.error.asUiMessage()
                snackbarManager.showMessage(uiMessage)
                _uiState.value = SubjectUiState.Error(uiMessage)
            } else {
                _uiState.value = SubjectUiState.Success(
                    subject = subject,
                    creditList = (creditListResult as? AppResult.Success)?.data,
                    photos = (photosResult as? AppResult.Success)?.data,
                    interests = (interestResult as AppResult.Success).data,
                    interestSortType = currentInterestSortType,
                    recommendations = (recommendationsResult as AppResult.Success).data,
                    reviews = (reviewsResult as AppResult.Success).data,
                    isLoggedIn = isLoggedIn
                )
            }
        }
    }

    private suspend fun fetchInterests(
        type: SubjectType,
        id: String,
        sortType: InterestSortType,
        status: SubjectInterestStatus,
    ): AppResult<SubjectInterestWithUserList> {
        return when (sortType) {
            InterestSortType.DEFAULT -> userSubjectRepository.getSubjectFollowingHotInterests(
                type,
                id,
                status
            )

            InterestSortType.HOT -> userSubjectRepository.getSubjectHotInterests(type, id, status)
        }
    }

    fun toggleInterestSortType(sortType: InterestSortType) {
        val currentState = _uiState.value
        if (currentState !is SubjectUiState.Success || currentInterestSortType == sortType) return

        viewModelScope.launch {
            currentInterestSortType = sortType

            val subject = currentState.subject
            val interestStatus =
                if (subject.isReleased) SubjectInterestStatus.MARK_STATUS_DONE else SubjectInterestStatus.MARK_STATUS_MARK

            val result = fetchInterests(subjectType, subjectId, sortType, interestStatus)

            if (result is AppResult.Success) {
                _uiState.value = currentState.copy(
                    interests = result.data,
                    interestSortType = sortType
                )
            } else if (result is AppResult.Error) {
                snackbarManager.showMessage(result.error.asUiMessage())
                currentInterestSortType = currentState.interestSortType
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            val currentLoginStatus = authRepository.isLoggedIn().first()
            triggerDataLoad(currentLoginStatus)
        }
    }

    fun updateStatus(newStatus: SubjectInterestStatus, rating: Int? = null) {
        val currentSuccessState = _uiState.value as? SubjectUiState.Success ?: return
        if (!currentSuccessState.isLoggedIn) return

        val originalSubject = currentSuccessState.subject

        viewModelScope.launch {
            val result: AppResult<Any> = when (newStatus) {
                SubjectInterestStatus.MARK_STATUS_UNMARK ->
                    userSubjectRepository.unmarkSubject(originalSubject.type, originalSubject.id)

                else ->
                    userSubjectRepository.addSubjectToInterests(
                        type = originalSubject.type,
                        id = originalSubject.id,
                        newStatus = newStatus,
                        rating = rating
                    )
            }

            when (result) {
                is AppResult.Success -> {
                    val confirmedInterest: SubjectInterest? = when (val data = result.data) {
                        is SubjectInterest -> data
                        is SubjectWithInterest<*> -> data.interest
                        else -> originalSubject.interest
                    }

                    val updatedSubject = when (originalSubject) {
                        is MovieDetail -> originalSubject.copy(interest = confirmedInterest)
                        is TvDetail -> originalSubject.copy(interest = confirmedInterest)
                        is BookDetail -> originalSubject.copy(interest = confirmedInterest)
                        is MusicDetail -> originalSubject.copy(interest = confirmedInterest)
                    }

                    _uiState.value = currentSuccessState.copy(subject = updatedSubject)
                }

                is AppResult.Error -> {
                    snackbarManager.showMessage(result.error.asUiMessage())
                }
            }
        }
    }

    fun collect() = collectionHandler.showCollectDialog(subjectType.toCollectType(), subjectId)
    fun dismissCollectDialog() = collectionHandler.dismissCollectDialog()
    fun showCreateDialog() = collectionHandler.showCreateDialog()
    fun dismissCreateDialog() = collectionHandler.dismissCreateDialog()

    fun createAndCollect(title: String) {
        viewModelScope.launch {
            collectionHandler.createAndCollect(title, subjectType.toCollectType(), subjectId)
        }
    }

    fun toggleCollection(douList: ItemDouList) {
        viewModelScope.launch {
            collectionHandler.toggleCollection(subjectType.toCollectType(), subjectId, douList)
        }
    }

    private fun SubjectType.toCollectType(): CollectType {
        return when (this) {
            SubjectType.MOVIE -> CollectType.MOVIE
            SubjectType.TV -> CollectType.TV
            SubjectType.BOOK -> CollectType.BOOK
            SubjectType.MUSIC -> CollectType.MUSIC
            else -> throw IllegalArgumentException()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(subjectId: String, subjectType: SubjectType): SubjectViewModel
    }
}