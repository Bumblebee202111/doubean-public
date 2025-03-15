package com.github.bumblebee202111.doubean.feature.subjects.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCommonRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.model.SubjectModule
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val userSubjectRepository: UserSubjectRepository,
    private val authRepository: AuthRepository,
    private val subjectCommonRepository: SubjectCommonRepository,
) : ViewModel() {

    private val _myBooksUiState: MutableStateFlow<MySubjectUiState> =
        MutableStateFlow(MySubjectUiState.Loading)
    val myBooksUiState = _myBooksUiState.asStateFlow()

    private val modulesResult = flow {
        emit(subjectCommonRepository.getSubjectModules(SubjectType.BOOK))
    }

    val isLoggedIn = authRepository.isLoggedIn()

    val booksUiState = combine(
        isLoggedIn,
        modulesResult
    ) { isLoggedIn, modulesResult ->
        when {
            modulesResult.isSuccess -> {
                BooksUiState.Success(
                    modules = modulesResult.getOrThrow(),
                    isLoggedIn = isLoggedIn
                )
            }

            else ->
                BooksUiState.Error
        }
    }.stateInUi(BooksUiState.Loading)

    init {
        getMyBooks()
    }

    private fun getMyBooks() {
        viewModelScope.launch {
            authRepository.observeLoggedInUserId().onEach { userId ->
                _myBooksUiState.value = when (userId) {
                    null -> MySubjectUiState.NotLoggedIn
                    else -> {
                        val result = userSubjectRepository.getUserSubjects(
                            userId =
                            userId
                        )
                        when (result.isSuccess) {
                            true ->
                                MySubjectUiState.Success(
                                    userId = userId,
                                    mySubject = result.getOrThrow()
                                        .first { it.type == SubjectType.BOOK }
                                )
                            false -> MySubjectUiState.Error
                        }
                    }
                }
            }.collectLatest { }
        }
    }

}

sealed interface BooksUiState {
    data class Success(
        val modules: List<SubjectModule>,
        val isLoggedIn: Boolean,
    ) : BooksUiState

    data object Error : BooksUiState
    data object Loading : BooksUiState
}