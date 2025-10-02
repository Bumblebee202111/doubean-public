package com.github.bumblebee202111.doubean.feature.subjects

import androidx.lifecycle.ViewModel
import com.github.bumblebee202111.doubean.domain.usecase.ObserveCurrentUserUseCase
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser = observeCurrentUserUseCase().stateInUi()
}