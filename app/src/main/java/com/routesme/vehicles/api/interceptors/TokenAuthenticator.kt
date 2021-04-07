package com.routesme.vehicles.api.interceptors

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.routesme.vehicles.App
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
    private val baseUrl = Helper.getConfigValue("baseUrl", R.raw.config)!!
    override fun authenticate(route: Route?, response: Response): Request? = when {

        response.networkResponse()?.request()?.url().toString() == baseUrl + "authentications/renewals" -> null
        response.networkResponse()?.request()?.url().toString() == baseUrl + "authentications" -> null
        else -> {

            val authorizationHeader: String? = response.networkResponse()?.request()?.headers()?.get("Authorization")

            if (authorizationHeader == null){
                //If the request redirects [ If authorization header is null ], So I'll add the authorization header again to it , then execute it again
                response.request().reAddAuthorizationHeader()
            }else{
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

    private fun Request.reAddAuthorizationHeader(): Request {

        return newBuilder()
                .addHeader(Header.Authorization.toString(), Account().accessToken.toString())
                .build()
    }
}