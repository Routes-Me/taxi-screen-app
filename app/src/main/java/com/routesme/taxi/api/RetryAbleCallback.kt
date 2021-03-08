package com.routesme.taxi.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

abstract class RetryAbleCallback<T>(private val call: Call<T>, private val repeatingDelay: Long) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (APIHelper.isCallSuccess(response) || APIHelper.isCallNotAcceptable(response)) {
            onFinalResponse(call, response)
        } else {
            Timer("RetryAbleCall", true).apply { schedule(TimeUnit.MINUTES.toMillis(repeatingDelay)) { retry() } }
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        Timer("RetryAbleCall", true).apply { schedule(TimeUnit.MINUTES.toMillis(repeatingDelay)) { retry() } }
    }

    open fun onFinalResponse(call: Call<T>?, response: Response<T>?) {}
    open fun onFinalFailure(call: Call<T>?, t: Throwable?) {}

    private fun retry() {
        call.clone().enqueue(this)
    }
}