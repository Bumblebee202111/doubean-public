package com.github.bumblebee202111.doubean.feature.subjects.movie

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.DouListRepository
import com.github.bumblebee202111.doubean.data.repository.ItemDouListRepository
import com.github.bumblebee202111.doubean.data.repository.MovieRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCommonRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.common.CollectionHandler
import com.github.bumblebee202111.doubean.feature.subjects.movie.navigation.MovieRoute
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
class MovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val userSubjectRepository: UserSubjectRepository,
    private val subjectCommonRepository: SubjectCommonRepository,
    private val authRepository: AuthRepository,
    itemDouListRepository: ItemDouListRepository,
    douListRepository: DouListRepository,
    savedStateHandle: SavedStateHandle,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {
    private val movieId = savedStateHandle.toRoute<MovieRoute>().movieId

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

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
            _uiState.value = MovieUiState.Loading

            val movieResultDeferred = async { movieRepository.getMovie(movieId) }
            val interestsResultDeferred = async {
                userSubjectRepository.getSubjectDoneFollowingHotInterests(
                    SubjectType.MOVIE,
                    movieId
                )
            }
            val photosResultDeferred = async { movieRepository.getPhotos(movieId) }
            val recommendationsDeferred = async {
                subjectCommonRepository.getSubjectRelatedItems(SubjectType.MOVIE, movieId)
            }
            val reviewsResultDeferred = async {
                subjectCommonRepository.getSubjectReviews(
                    subjectType = SubjectType.MOVIE,
                    subjectId = movieId
                )
            }

            val movieResult = movieResultDeferred.await()
            val interestResult = interestsResultDeferred.await()
            val photosResult = photosResultDeferred.await()
            val recommendationsResult = recommendationsDeferred.await()
            val reviewsResult = reviewsResultDeferred.await()

            val results = listOf(
                movieResult,
                interestResult,
                photosResult,
                recommendationsResult,
                reviewsResult
            )

            val firstError = results.filterIsInstance<AppResult.Error>().firstOrNull()

            if (firstError != null) {
                val uiMessage = firstError.error.asUiMessage()
                snackbarManager.showMessage(uiMessage)
                _uiState.value = MovieUiState.Error(uiMessage)
            } else {
                _uiState.value = MovieUiState.Success(
                    movie = (movieResult as AppResult.Success).data,
                    interests = (interestResult as AppResult.Success).data,
                    photos = (photosResult as AppResult.Success).data,
                    recommendations = (recommendationsResult as AppResult.Success).data,
                    reviews = (reviewsResult as AppResult.Success).data,
                    isLoggedIn = isLoggedIn
                )
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            val currentLoginStatus = authRepository.isLoggedIn().first()
            triggerDataLoad(currentLoginStatus)
        }
    }

    fun updateStatus(newStatus: SubjectInterestStatus) {
        val currentSuccessState = _uiState.value as? MovieUiState.Success ?: return

        if (!currentSuccessState.isLoggedIn) {
            return
        }

        val originalMovie = currentSuccessState.movie

        viewModelScope.launch {
            val result: AppResult<Any> = when (newStatus) {
                SubjectInterestStatus.MARK_STATUS_UNMARK ->
                    userSubjectRepository.unmarkSubject(originalMovie.type, originalMovie.id)

                else ->
                    userSubjectRepository.addSubjectToInterests(
                        type = originalMovie.type,
                        id = originalMovie.id,
                        newStatus = newStatus
                    )
            }

            when (result) {
                is AppResult.Success -> {
                    val confirmedInterest = when (val data = result.data) {
                        is SubjectInterest -> data
                        is SubjectWithInterest<*> -> data.interest
                        else -> {

                            originalMovie.interest
                        }
                    }
                    _uiState.value = currentSuccessState.copy(
                        movie = originalMovie.copy(interest = confirmedInterest)
                    )
                }

                is AppResult.Error -> {
                    snackbarManager.showMessage(result.error.asUiMessage())
                }
            }
        }
    }

    fun collect() {
        collectionHandler.showCollectDialog(CollectType.MOVIE, movieId)
    }

    fun dismissCollectDialog() = collectionHandler.dismissCollectDialog()

    fun showCreateDialog() = collectionHandler.showCreateDialog()

    fun dismissCreateDialog() = collectionHandler.dismissCreateDialog()

    fun createAndCollect(title: String) {
        viewModelScope.launch {
            collectionHandler.createAndCollect(
                title = title,
                type = CollectType.MOVIE,
                id = movieId
            )
        }
    }

    fun toggleCollection(douList: ItemDouList) {
        viewModelScope.launch {
            collectionHandler.toggleCollection(
                type = CollectType.MOVIE,
                id = movieId,
                douList = douList
            )
        }
    }
}
