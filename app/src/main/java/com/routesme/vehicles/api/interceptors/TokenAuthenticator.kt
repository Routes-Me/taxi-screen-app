package com.routesme.vehicles.api.interceptors

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.routesme.vehicles.App
import com.routesme.vehicles.BuildConfig
import com.routesme.vehicles.R
import com.routesme.vehicles.api.Constants
import com.routesme.vehicles.api.Header
import com.routesme.vehicles.helper.Helper
import com.routesme.vehicles.uplevels.Account
import com.routesme.vehicles.view.activity.RefreshTokenActivity
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route


class TokenAuthenticator(private val context: Context): Authenticator{
    private val baseUrl = BuildConfig.OLD_PRODUCTION_BASE_URL
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
            // Log.d("UnAuthorizationRequest", "Auth header: ${response.request().headers().get("Authorization")}")
            //  null

            val authorizationHeader: String? = response.networkResponse()?.request()?.headers()?.get("Authorization")

            //Here.. check if the request redirects or the access token expired ..
            if (authorizationHeader == null){
                //If the request redirects [ If authorization header is null ], So I'll add the authorization header again to it , then execute it again
                response.request().reAddAuthorizationHeader()
            }else{
                Log.d("RefreshTokenTesting", "TokenAuthenticator... Code: ${response.code()}")
                //If it's expired, So I'll handle refresh token logic
                if (!App.instance.isRefreshActivityAlive) {
                    App.instance.isRefreshActivityAlive = true
                    openRefreshTokenActivity()
                }
                null
            }
        }
    }

    private fun openRefreshTokenActivity() {
        if (context is Activity){
            val activity: Activity = context
            activity.startActivity(Intent(activity, RefreshTokenActivity::class.java))
            activity.finish()
        }
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
        Log.d("reAddAuthorizationHeader","Add token Again, Token: ${Account().accessToken.toString()}, Url: ${url()}")
        return newBuilder()
                // .removeHeader(Header.Authorization.toString())
                .addHeader(Header.Authorization.toString(), Account().accessToken.toString())
                .build()
    }
}