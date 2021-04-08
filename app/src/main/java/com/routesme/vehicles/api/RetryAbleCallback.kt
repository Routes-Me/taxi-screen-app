package com.routesme.vehicles.api

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

abstract class RetryAbleCallback<T>(private val call: Call<T>, private val repeatingDelayInMinutes: Long) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (APIHelper.isCallSuccess(response) || APIHelper.isCallNotAcceptable(response)) {
            onFinalResponse(call, response)
        } else {
            Timer("RetryAbleCall", true).apply { schedule(TimeUnit.MINUTES.toMillis(repeatingDelayInMinutes)) { retry() } }
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {

        Timer("RetryAbleCall", true).apply { schedule(TimeUnit.MINUTES.toMillis(repeatingDelayInMinutes)) { retry() } }
    }

    open fun onFinalResponse(call: Call<T>?, response: Response<T>?) {}
    open fun onFinalFailure(call: Call<T>?, t: Throwable?) {}

    private fun retry() {
        call.clone().enqueue(this)
    }
}