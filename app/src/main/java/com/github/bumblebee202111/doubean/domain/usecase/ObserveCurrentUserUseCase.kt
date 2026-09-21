package com.github.bumblebee202111.doubean.domain.usecase

import com.github.bumblebee202111.doubean.shared.auth.data.AuthRepository
import com.github.bumblebee202111.doubean.shared.user.data.UserRepository
import com.github.bumblebee202111.doubean.shared.user.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
class ObserveCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.loggedInUserId.flatMapLatest { userId ->
            if (userId != null) {
                userRepository.getCachedUser(userId)
            } else {
                flowOf(null)
            }
        }
    }
}