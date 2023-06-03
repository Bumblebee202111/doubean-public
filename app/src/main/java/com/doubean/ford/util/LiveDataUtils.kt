package com.doubean.ford.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData


object LiveDataUtils {

    //TODO: use Flow instead of LiveData to process complex business logic
    inline fun <reified T> zip(vararg liveItems: LiveData<T>): LiveData<List<T>> {
        val zipped = arrayOfNulls<T>(liveItems.size)
        val mediator = MediatorLiveData<List<T>>()
        for (i in liveItems.indices) {
            mediator.addSource(liveItems[i]) { o ->
                zipped[i] = o
                if (!zipped.contains(null)) {
                    val value = List<T>(liveItems.size) { zipped[i]!! }
                    mediator.value = value
                }
            }
        }
        return mediator
    }


    /**
     * From iosched
     * Combines this [LiveData] with another [LiveData] using the specified [combiner] and returns the
     * result as a [LiveData].
     */
    fun <A, B, Result> LiveData<A>.combine(
        other: LiveData<B>,
        combiner: (A, B) -> Result,
    ): LiveData<Result> {
        val result = MediatorLiveData<Result>()
        result.addSource(this) { a ->
            val b = other.value
            if (b != null) {
                result.postValue(combiner(a, b))
            }
        }
        result.addSource(other) { b ->
            val a = this@combine.value
            if (a != null) {
                result.postValue(combiner(a, b))
            }
        }
        return result
    }

    /**
     * Combines this [LiveData] with other two [LiveData]s using the specified [combiner] and returns
     * the result as a [LiveData].
     */
    fun <A, B, C, Result> LiveData<A>.combine(
        other1: LiveData<B>,
        other2: LiveData<C>,
        combiner: (A, B, C) -> Result,
    ): LiveData<Result> {
        val result = MediatorLiveData<Result>()
        result.addSource(this) { a ->
            val b = other1.value
            val c = other2.value
            if (b != null && c != null) {
                result.postValue(combiner(a, b, c))
            }
        }
        result.addSource(other1) { b ->
            val a = this@combine.value
            val c = other2.value
            if (a != null && c != null) {
                result.postValue(combiner(a, b, c))
            }
        }
        result.addSource(other2) { c ->
            val a = this@combine.value
            val b = other1.value
            if (a != null && b != null) {
                result.postValue(combiner(a, b, c))
            }
        }
        return result
    }
}
