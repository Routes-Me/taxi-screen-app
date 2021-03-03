package com.routesme.taxi.api

import android.app.Activity
import android.content.Context
import com.google.gson.GsonBuilder
import com.routesme.taxi.view.activity.LoginActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit


class ApiWorker(val context: Context) {

    private var mClient: OkHttpClient? = null
    private var mGsonConverter: GsonConverterFactory? = null
    private val interceptor = HttpLoggingInterceptor()

    val client: OkHttpClient
        @Throws(NoSuchAlgorithmException::class, KeyManagementException::class)
        get() {
            if (mClient == null) {
                 mClient = OkHttpClient.Builder().apply {
                     connectTimeout(1, TimeUnit.MINUTES)
                     readTimeout(30, TimeUnit.SECONDS)
                     writeTimeout(15, TimeUnit.SECONDS)
                     addInterceptor(interceptor.setLevel(HttpLoggingInterceptor.Level.BODY))
                     addInterceptor(BasicAuthInterceptor(context as Activity))
                     addInterceptor(NotAcceptableRefreshTokenInterceptor(context))
                     authenticator(TokenRefreshAuthenticator(context))
                  //  if (context !is LoginActivity) addInterceptor(UnauthorizedInterceptor(context))
                 }.build()
            }
            return mClient!!
        }

    val gsonConverter: GsonConverterFactory?
        get() {
            if (mGsonConverter == null)
                mGsonConverter = GsonConverterFactory.create(GsonBuilder().setLenient().disableHtmlEscaping().create())

            return mGsonConverter
        }
}