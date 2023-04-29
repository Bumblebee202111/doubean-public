package com.doubean.ford.ui.common

class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
    private var handledError = false
    val errorMessageIfNotHandled: String?
        get() {
            if (handledError) {
                return null
            }
            handledError = true
            return errorMessage
        }
}