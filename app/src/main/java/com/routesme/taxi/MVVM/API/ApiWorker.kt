package com.routesme.taxi.MVVM.API

import android.app.Activity
import android.content.Context
import com.google.gson.GsonBuilder
import com.routesme.taxi.MVVM.View.activity.LoginActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit


class ApiWorker(val context: Context) {

    private var mClient: OkHttpClient? = null
    private var mGsonConverter: GsonConverterFactory? = null
    val client: OkHttpClient
        @Throws(NoSuchAlgorithmException::class, KeyManagementException::class)
        get() {
            if (mClient == null) {
                 mClient = OkHttpClient.Builder().apply {
                     connectTimeout(1, TimeUnit.MINUTES)
                     readTimeout(30, TimeUnit.SECONDS)
                     writeTimeout(15, TimeUnit.SECONDS)
                     addInterceptor(BasicAuthInterceptor(context as Activity))
                     if (context !is LoginActivity) addInterceptor(UnauthorizedInterceptor(context))
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