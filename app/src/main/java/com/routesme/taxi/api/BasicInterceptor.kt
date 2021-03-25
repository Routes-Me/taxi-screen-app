package com.routesme.taxi.api

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.routesme.taxi.App
import com.routesme.taxi.BuildConfig
import com.routesme.taxi.helper.AdminConsoleHelper
import com.routesme.taxi.uplevels.Account
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection

internal class BasicAuthInterceptor() : Interceptor {

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
    private fun countryCode() = App.instance.resources.configuration.locales.get(0).country

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


enum class Header {
    Authorization, CountryCode, AppVersion, application
}