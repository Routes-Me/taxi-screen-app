package com.routesme.taxi_screen.kotlin.MVVM.API

import android.content.Context
import com.google.gson.JsonElement
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.routesme.taxi_screen.kotlin.Class.Helper
import com.routesme.taxi_screen.kotlin.MVVM.Model.RegistrationCredentials
import com.routesme.taxi_screen.kotlin.MVVM.Model.SignInCredentials
import com.routesme.taxi_screen.kotlin.Model.*
import com.routesme.taxiscreen.R
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*


interface RestApiService {

    @POST("v1/signin")
    fun signIn(@Body signInCredentials: SignInCredentials): Call<JsonElement>


    @POST("devices")
    fun register(@Body registrationCredentials: RegistrationCredentials): Call<JsonElement>

    @GET("institutions")
    fun getInstitutions(@Query("offset") offset: Int, @Query("limit") limit: Int): Call<JsonElement>

    @GET("vehicles/{institutionId}")
    fun getVehicles(@Path("institutionId") institutionId: Int, @Query("offset") offset: Int, @Query("limit") limit: Int): Call<JsonElement>



    //Advertisements...
    @GET("Channels")
    fun getVideos(@Query("channelidvideolist") ch_ID_Videos: Int): Call<List<VideoModel>>

    @GET("Channels")
    fun getBanners(@Query("channelidadvlist") ch_ID_Banners: Int): Call<List<BannerModel>>

    @GET("Contents")
    fun getContent(): Call<ContentResponse>

    companion object {
        fun createCorService(context: Context): RestApiService {
            return Retrofit.Builder()
                    .baseUrl(Helper.getConfigValue("baseUrl", R.raw.config)!!)
                    .addConverterFactory(ApiWorker(context).gsonConverter!!)
                    .client(ApiWorker(context).client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build().create(RestApiService::class.java)
        }
    }
}