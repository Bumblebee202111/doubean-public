package com.github.bumblebee202111.doubean.feature.app

import androidx.lifecycle.ViewModel
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserRepository
import com.github.bumblebee202111.doubean.model.fangorns.User
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class BottomNavViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser: StateFlow<User?> =
        authRepository.observeLoggedInUserId().flatMapLatest { userId ->
            if (userId == null) {
                flowOf(null)
            } else {
                userRepository.getCachedUser(userId)
            }
        }.stateInUi()
}