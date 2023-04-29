package com.doubean.ford.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

//TODO: use Flow instead of LiveData to process complex data logic
object LiveDataUtil {
    inline fun <reified T> zip(vararg liveItems: LiveData<T>): LiveData<List<T>> {
        val zipped = arrayOfNulls<T>(liveItems.size)
        val mediator = MediatorLiveData<List<T>>()
        for (i in liveItems.indices) {
            mediator.addSource(liveItems[i]) { o: T ->
                zipped[i] = o
                if (!zipped.contains(null)) {
                    val value = List<T>(liveItems.size) { zipped[i]!! }
                    mediator.value = value
                }
            }
        }
        return mediator
    }
}