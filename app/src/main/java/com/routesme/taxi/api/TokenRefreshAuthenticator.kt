package com.routesme.taxi.api

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.routesme.taxi.App
import com.routesme.taxi.R
import com.routesme.taxi.data.repository.TokenRepository
import com.routesme.taxi.helper.Helper
import com.routesme.taxi.uplevels.Account
import com.routesme.taxi.view.activity.RefreshTokenActivity
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticator(private val context: Context): Authenticator{
    private val baseUrl = Helper.getConfigValue("baseUrl", R.raw.config)!!
    override fun authenticate(route: Route?, response: Response): Request? = when {

/*
        response.networkResponse()?.request()?.url().toString() == baseUrl + "authentications/renewals" && response.code() == HttpURLConnection.HTTP_NOT_FOUND -> {
            Log.d("RefreshToken", "Renewals request .. response code: ${response.code()}")
            null
        }

        response.networkResponse()?.request()?.url().toString() == baseUrl + "authentications/renewals" && response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE -> {
           // logOutAuthenticator()
            null
        }
        */

        response.networkResponse()?.request()?.url().toString() == baseUrl + "authentications/renewals" -> null
        response.networkResponse()?.request()?.url().toString() == baseUrl + "authentications" -> null
        //retryCount(response.request()) == 1 -> null
        else -> {
            if (!App.instance.isRefreshActivityAlive) {
                App.instance.isRefreshActivityAlive = true
                openRefreshTokenActivity()
            }
            null
        }//response.createSignedRequest()
    }

    private fun openRefreshTokenActivity() {
        context.startActivity(Intent(context, RefreshTokenActivity::class.java))
        (context as Activity).finish()
    }

    private fun retryCount(request: Request?)= request?.header(Constants.httpHeaderRetryCount)?.toInt() ?: 0

    private fun Response.createSignedRequest(): Request? {
        val refreshTokenResponse = TokenRepository(context).refreshToken()
        val accessToken = refreshTokenResponse.value?.accessToken
        val refreshToken = refreshTokenResponse.value?.refreshToken
        accessToken?.let { Account().accessToken = it}
        refreshToken?.let { Account().refreshToken = it }
        return request().signWithToken()
    }
    private fun Request.signWithToken(): Request {
  //  Log.d("Retry-Count", "Request: ${this.url()}, Count times: ${retryCount(this)}")
   return newBuilder()
            .header(Constants.httpHeaderRetryCount, "${retryCount(this) + 1}")
            .removeHeader(Header.Authorization.toString())
            .addHeader(Header.Authorization.toString(), Account().accessToken.toString())
            .build()
}
}