package com.routesme.taxi.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

object APIHelper {
    fun <T> enqueueWithRetry(call: Call<T>, repeatingDelay: Long, callback: Callback<T>) {
        call.enqueue(object : RetryAbleCallback<T>(call, repeatingDelay) {
            override fun onFinalResponse(call: Call<T>?, response: Response<T>?) {
                callback.onResponse(call, response)
            }

            override fun onFinalFailure(call: Call<T>?, t: Throwable?) {
                callback.onFailure(call, t)
            }
        })
    }

    fun isCallSuccess(response: Response<*>) = response.code() in 200..399
    fun isCallNotAcceptable(response: Response<*>) = response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE
}