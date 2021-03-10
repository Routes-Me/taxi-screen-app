package com.routesme.taxi.api.interceptors

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.routesme.taxi.R
import com.routesme.taxi.api.Constants
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
            //Here.. check if the request redirects or the access token expired ..

            //If the request redirects, So I'll add the authorization header again to if , then execute it again
            response.request().reAddAuthorizationHeader()

            //If it's expired, So I'll handle refresh token logic

            if (!App.instance.isRefreshActivityAlive) {
                App.instance.isRefreshActivityAlive = true
                openRefreshTokenActivity()
            }
            null

        }
    }

    private fun openRefreshTokenActivity() {
        context.startActivity(Intent(context, RefreshTokenActivity::class.java))
        (context as Activity).finish()
    }

    private fun retryCount(request: Request?)= request?.header(Constants.httpHeaderRetryCount)?.toInt() ?: 0
/*
    private fun Response.createSignedRequest(): Request? {
        val refreshTokenResponse = TokenRepository(context).refreshToken()
        val accessToken = refreshTokenResponse.value?.accessToken
        val refreshToken = refreshTokenResponse.value?.refreshToken
        accessToken?.let { Account().accessToken = it}
        refreshToken?.let { Account().refreshToken = it }
        return request().signWithToken()
    }
    */

    private fun Request.reAddAuthorizationHeader(): Request {
    Log.d("UnAuthorizationRequest","Add token Again, Token: ${Account().accessToken.toString()}, Url: ${url()}")
   return newBuilder()
           // .removeHeader(Header.Authorization.toString())
            .addHeader(Header.Authorization.toString(), Account().accessToken.toString())
            .build()
}

}