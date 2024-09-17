package com.example.youtubedownloader

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data:T?=null
//    runBlocking(Dispatchers.Main) {

    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {

        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this@getOrAwaitValue.observeForever(observer)
    afterObserve.invoke()

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        this@getOrAwaitValue.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }
//    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}