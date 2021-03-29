package com.routesme.taxi.api.interceptors

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.routesme.taxi.BuildConfig
import com.routesme.taxi.R
import com.routesme.taxi.helper.AdminConsoleHelper
import com.routesme.taxi.helper.Helper
import com.routesme.taxi.uplevels.Account
import com.routesme.taxi.view.activity.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection

internal class BasicAuthInterceptor(val context: Context) : Interceptor {

    @TargetApi(Build.VERSION_CODES.N)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
                .addHeader(Header.Authorization.toString(), Account().accessToken.toString())
                .addHeader(Header.CountryCode.toString(), countryCode())
                .addHeader(Header.AppVersion.toString(), appVersion())
                .addHeader(Header.application.toString(), "screen")
                .build()
        return chain.proceed(request)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun countryCode() = context.resources.configuration.locales.get(0).country

    private fun appVersion() = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
}

internal class UnauthorizedInterceptor(val activity: Activity) : Interceptor {
    private val AUTHORIZATION_KAY = "authorization"
    private var adminConsoleHelper = AdminConsoleHelper(activity)
    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) openModelPresenterScreen(activity, response.code())
        return response
    }

    private fun openModelPresenterScreen(activity: Activity, responseCode: Int) {
        adminConsoleHelper.logOff()
    }
}

internal class NotAcceptableRefreshTokenInterceptor(val context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        val baseUrl = Helper.getConfigValue("baseUrl", R.raw.config)!!
        val response: Response = chain.proceed(chain.request())
        val request = response.request()
        if (response.code() == HttpURLConnection.HTTP_NOT_FOUND && request.url().toString() == baseUrl + "authentications/renewals") {
            Log.d("RefreshToken", "${request.url()} not found !")

            //  val currentActivity: Activity = (ApplicationProvider.getApplicationContext() as MyApp).getCurrentActivity()

            context.startActivity(Intent(context, LoginActivity::class.java))
            (context as Activity).finish()
        }
        return response
    }
}

enum class Header { Authorization, CountryCode, AppVersion, application }