package com.routesme.taxi_screen.kotlin.Server

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.routesme.taxi_screen.kotlin.Model.Authorization
import com.routesme.taxi_screen.kotlin.View.ModelPresenter
import com.routesme.taxiscreen.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class BasicAuthInterceptor(val activity: Activity) : Interceptor {
    @TargetApi(Build.VERSION_CODES.N)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request().newBuilder()
                .addHeader(Header.Authorization.toString(), token())
                .addHeader(Header.country_code.toString(), countryCode())
                .addHeader(Header.app_version.toString(), appVersion())
                .build()
        return chain.proceed(request)
    }

    private fun token() = "Bearer ${activity.getSharedPreferences("userData", Activity.MODE_PRIVATE).getString("tabToken", null)}"

    @RequiresApi(Build.VERSION_CODES.N)
    private fun countryCode() = activity.resources.configuration.locales.get(0).country

    private fun appVersion() = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
}

internal class UnauthorizedInterceptor(val activity: Activity) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        if (response.code() == 401) openModelPresenterScreen(activity, response.code())

        return response
    }

    private fun openModelPresenterScreen(activity: Activity, responseCode: Int) {
        activity.startActivity(Intent(activity, ModelPresenter::class.java).putExtra("authorization", Authorization(false, responseCode)))
        activity.finish()
    }
}

enum class Header {
    Authorization, country_code, app_version
}