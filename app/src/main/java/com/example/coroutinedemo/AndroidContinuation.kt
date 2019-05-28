package com.example.coroutinedemo

import android.os.Handler
import android.os.Looper
import kotlin.coroutines.*

private class AndroidContinuation<in T>(val cont: Continuation<T>) : Continuation<T> by cont {

    override fun resumeWith(result: Result<T>) {
        result.fold(onSuccess = {value ->
            if(Looper.myLooper() == Looper.getMainLooper()) cont.resume(value)
            else Handler(Looper.getMainLooper()).post { cont.resume(value) }
        }, onFailure = {exception ->
            if(Looper.myLooper() == Looper.getMainLooper()) cont.resumeWithException(exception)
            else Handler(Looper.getMainLooper()).post { cont.resumeWithException(exception) }
        })
    }
}

/**
 * Android context, provides an AndroidContinuation, executes everything on the UI Thread
 */
object Android : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
        AndroidContinuation(continuation)
}
