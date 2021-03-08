package com.routesme.taxi.api

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

abstract class RetryAbleCallback<T>(private val call: Call<T>, totalRetries: Int) : Callback<T> {
    private var totalRetries = 3
    private var retryCount = 0

    init {
        this.totalRetries = totalRetries
    }

    companion object {
        private val TAG = RetryAbleCallback::class.java.simpleName
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (!APIHelper.isCallSuccess(response)) {
            Timer("RetryAbleCall", true).apply {
                schedule(TimeUnit.SECONDS.toMillis(5),TimeUnit.SECONDS.toMillis(5)) {
                   // if (retryCount++ < totalRetries) {
                        //Log.v(TAG, "Retrying API Call -  ($retryCount / $totalRetries)")
                        retry()
                   // } else onFinalResponse(call, response)
                }
            }
        } else {
            onFinalResponse(call, response)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.e(TAG, t.message)
        Timer("RetryAbleCall", true).apply {
            schedule(TimeUnit.SECONDS.toMillis(5),TimeUnit.SECONDS.toMillis(5)) {
               // if (retryCount++ < totalRetries) {
                  //  Log.v(TAG, "Retrying API Call -  ($retryCount / $totalRetries)")
                    retry()
              //  } else onFinalFailure(call, t)
            }
        }
    }

    open fun onFinalResponse(call: Call<T>?, response: Response<T>?) {}
    open fun onFinalFailure(call: Call<T>?, t: Throwable?) {}
    private fun retry() {
        call.clone().enqueue(this)
    }
}