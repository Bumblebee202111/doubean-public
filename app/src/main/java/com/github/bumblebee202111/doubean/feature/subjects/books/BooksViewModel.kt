package com.github.bumblebee202111.doubean.feature.subjects.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCommonRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.feature.subjects.SubjectModulesUiState
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BooksViewModel @Inject constructor(
    private val userSubjectRepository: UserSubjectRepository,
    private val authRepository: AuthRepository,
    private val subjectCommonRepository: SubjectCommonRepository,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    private val _myBooksUiState: MutableStateFlow<MySubjectUiState> =
        MutableStateFlow(MySubjectUiState.Loading)
    val myBooksUiState = _myBooksUiState.asStateFlow()

    private val modulesResult = retryTrigger.flatMapLatest {
        flow { emit(subjectCommonRepository.getSubjectModules(SubjectType.BOOK)) }
    }

    val isLoggedIn = authRepository.isLoggedIn()

    val modulesUiState = combine(
        isLoggedIn,
        modulesResult
    ) { isLoggedIn, result ->
        when (result) {
            is AppResult.Success -> {
                SubjectModulesUiState.Success(
                    modules = result.data,
                    isLoggedIn = isLoggedIn
                )
            }

            is AppResult.Error -> {
                val errorMessage = result.error.asUiMessage()
                SubjectModulesUiState.Error(errorMessage)
            }
        }
    }.stateInUi(SubjectModulesUiState.Loading)

    init {
        getMyBooks()
    }

    private fun getMyBooks() {
        viewModelScope.launch {
            authRepository.loggedInUserId.collectLatest { userId ->
                _myBooksUiState.value = when (userId) {
                    null -> MySubjectUiState.NotLoggedIn
                    else -> {
                        when (val result = userSubjectRepository.getUserSubjects(userId = userId)) {
                            is AppResult.Success ->
                                MySubjectUiState.Success(
                                    userId = userId,
                                    mySubject = result.data
                                        .first { it.type == SubjectType.BOOK }
                                )

                            is AppResult.Error -> {
                                snackbarManager.showMessage(result.error.asUiMessage())
                                MySubjectUiState.Error
                            }
                        }
                    }
                }
            }
        }
    }

    fun retry() {
        retryTrigger.value++
    }
}
