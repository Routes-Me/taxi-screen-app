package com.routesme.taxi.api.interceptors

import android.content.Context
import android.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ReceivedCookiesInterceptor(private val context: Context) : Interceptor {
    companion object {
        const val PREF_COOKIES = "PREF_COOKIES"
    }
   // @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (originalResponse.headers("Set-Cookie").isNotEmpty()) {
            val cookies = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(PREF_COOKIES, HashSet()) as HashSet<String>
            for (header in originalResponse.headers("Set-Cookie")) {
                cookies.add(header)
            }
            val memes = PreferenceManager.getDefaultSharedPreferences(context).edit()
            memes.putStringSet(PREF_COOKIES, cookies).apply()
            memes.commit()
        }
        return originalResponse
    }
}

class AddCookiesInterceptor(private val context: Context) : Interceptor {

    companion object {
        const val PREF_COOKIES = "PREF_COOKIES"
    }

   // @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        val preferences = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(PREF_COOKIES, HashSet()) as HashSet<String>
        val original: Request = chain.request()
        if (original.url().toString().contains("distributor")) {
            for (cookie in preferences) {
                builder.addHeader("Cookie", cookie)
            }
        }
        return chain.proceed(builder.build())
    }
}