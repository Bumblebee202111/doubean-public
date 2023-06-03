package com.doubean.ford.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {
    private val mText: MutableLiveData<String> = MutableLiveData("This is notifications fragment")

    val text: LiveData<String>
        get() = mText
}