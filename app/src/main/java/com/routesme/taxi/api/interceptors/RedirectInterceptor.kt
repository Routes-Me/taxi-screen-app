package com.routesme.taxi.api.interceptors

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class RedirectInterceptor (private val context: Context) : Interceptor {
    companion object {
        const val PREF_COOKIES = "PREF_COOKIES"
    }
    // @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {



        return null
    }
}