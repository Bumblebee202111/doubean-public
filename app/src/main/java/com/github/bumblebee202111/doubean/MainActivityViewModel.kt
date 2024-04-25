package com.github.bumblebee202111.doubean

import androidx.lifecycle.ViewModel
import com.github.bumblebee202111.doubean.coroutines.AppDispatchers
import com.github.bumblebee202111.doubean.coroutines.Dispatcher
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    preferenceStorage: PreferenceStorage,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) :
    ViewModel() {
    val enableNotifications =
        preferenceStorage.preferToReceiveNotifications.flowOn(ioDispatcher).stateInUi()

    val startAppWithGroups = preferenceStorage.startAppWithGroups.flowOn(ioDispatcher).stateInUi()

}