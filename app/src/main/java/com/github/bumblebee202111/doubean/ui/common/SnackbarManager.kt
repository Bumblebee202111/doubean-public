package com.github.bumblebee202111.doubean.ui.common

import com.github.bumblebee202111.doubean.ui.model.UiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarManager @Inject constructor() {
    private val _currentMessage = MutableStateFlow<UiMessage?>(null)
    val currentMessage: StateFlow<UiMessage?> = _currentMessage

    fun showSnackBar(message: UiMessage) {
        _currentMessage.value = message
    }

    fun messageShown() {
        if (_currentMessage.value != null) {
            _currentMessage.value = null
        }
    }
}