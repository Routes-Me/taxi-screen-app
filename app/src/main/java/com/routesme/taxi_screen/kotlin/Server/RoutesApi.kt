package com.routesme.taxi_screen.kotlin.Server

import com.google.gson.JsonElement
import com.routesme.taxi_screen.kotlin.MVVM.Model.SignInCredentials
import com.routesme.taxi_screen.kotlin.Model.*
import retrofit2.Call
import retrofit2.http.*

interface RoutesApi {


    @POST("v1/signin")
    fun signIn(@Body signInCredentials: SignInCredentials?): Call<JsonElement>

    @POST("devices")
    fun register(@Body registrationCredentials: RegistrationCredentials?): Call<JsonElement>

    //Advertisements...
    @GET("Channels")
    fun getVideos(@Query("channelidvideolist") ch_ID_Videos: Int): Call<List<VideoModel>>

    @GET("Channels")
    fun getBanners(@Query("channelidadvlist") ch_ID_Banners: Int): Call<List<BannerModel>>

    @GET("Contents")
    fun getContent(): Call<ContentResponse>
}