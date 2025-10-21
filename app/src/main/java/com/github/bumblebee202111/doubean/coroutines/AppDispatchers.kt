

package com.github.bumblebee202111.doubean.coroutines

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val appDispatcher: AppDispatchers)

enum class AppDispatchers {
    Default,
    IO,
    Main
}
