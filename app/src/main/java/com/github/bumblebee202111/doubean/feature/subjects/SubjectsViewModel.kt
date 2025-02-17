package com.github.bumblebee202111.doubean.feature.subjects

import androidx.lifecycle.ViewModel
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserRepository
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    authRepository: AuthRepository,
    userRepository: UserRepository,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser = authRepository.observeLoggedInUserId().flatMapLatest {
        if (it == null) {
            emptyFlow()
        } else {
            userRepository.getCachedUser(it)
        }
    }.stateInUi()
}