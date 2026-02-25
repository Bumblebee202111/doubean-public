package com.github.bumblebee202111.doubean.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn


context(viewModel: ViewModel)
fun <T> Flow<T>.stateInUi(
): StateFlow<T?> {
    return stateIn(viewModel.viewModelScope, SharingStarted.WhileSubscribed(5000L), null)
}

context(viewModel: ViewModel)
fun <T : Any> Flow<T>.stateInUi(
    initialValue: T,
): StateFlow<T> {
    return stateIn(viewModel.viewModelScope, SharingStarted.WhileSubscribed(5000L), initialValue)
}
