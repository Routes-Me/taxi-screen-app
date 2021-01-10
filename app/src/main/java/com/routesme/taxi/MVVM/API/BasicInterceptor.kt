package com.routesme.taxi.MVVM.API

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.routesme.taxi.AdminConsolePanel.Class.AdminConsoleHelper
import com.routesme.taxi.MVVM.Model.Authorization
import com.routesme.taxi.MVVM.View.activity.ModelPresenter
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.BuildConfig
import com.routesme.taxi.MVVM.View.activity.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
internal class BasicAuthInterceptor(val activity: Activity) : Interceptor {

    @TargetApi(Build.VERSION_CODES.N)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
                .addHeader(Header.Authorization.toString(), token())
                .addHeader(Header.CountryCode.toString(), countryCode())
                .addHeader(Header.AppVersion.toString(), appVersion())
                .addHeader("application", "c2NyZWVu")
                .build()
        return chain.proceed(request)
    }

    private fun token() = "Bearer ${activity.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE).getString(SharedPreferencesHelper.token, null)}"

    @RequiresApi(Build.VERSION_CODES.N)
    private fun countryCode() = activity.resources.configuration.locales.get(0).country

    private fun appVersion() = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
}

internal class UnauthorizedInterceptor(val activity: Activity) : Interceptor {
    private val AUTHORIZATION_KAY = "authorization"
    private var adminConsoleHelper = AdminConsoleHelper(activity)
    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        if (response.code() == 401) openModelPresenterScreen(activity, response.code())
        return response
    }

    private fun openModelPresenterScreen(activity: Activity, responseCode: Int) {
        adminConsoleHelper.logOff()
    }
}

enum class Header {
    Authorization, CountryCode, AppVersion
}