package com.routesme.taxi.api.interceptors

import com.routesme.taxi.uplevels.Account
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpCookie

class ReceivedCookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val setCookie = response.headers("Set-Cookie")
        if (!setCookie.isNullOrEmpty()) {
            val cookieList: List<HttpCookie> = HttpCookie.parse(setCookie.first())
            val refreshToken = cookieList.firstOrNull { it.name == "refreshToken" }?.value
            refreshToken?.let { Account().refreshToken = it }
        }
        return response
    }
}