package com.github.bumblebee202111.doubean.feature.subjects.tvs

import androidx.lifecycle.ViewModel
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCommonRepository
import com.github.bumblebee202111.doubean.feature.subjects.SubjectModulesUiState
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class TvsViewModel @Inject constructor(
    private val subjectCommonRepository: SubjectCommonRepository,
    authRepository: AuthRepository,
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val modulesResult = retryTrigger.flatMapLatest {
        flow {
            emit(subjectCommonRepository.getSubjectModules(SubjectType.TV))
        }
    }

    val isLoggedIn = authRepository.isLoggedIn()

    val modulesUiState = combine(
        isLoggedIn,
        modulesResult,
    ) { isLoggedIn, result ->
        when (result) {
            is AppResult.Success -> {
                SubjectModulesUiState.Success(modules = result.data, isLoggedIn = isLoggedIn)
            }

            is AppResult.Error -> {
                val errorMessage = result.error.asUiMessage()
                SubjectModulesUiState.Error(errorMessage)
            }
        }
    }.stateInUi(SubjectModulesUiState.Loading)

    fun retry() {
        retryTrigger.value++
    }
}
