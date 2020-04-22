package com.routesme.taxi_screen.kotlin.Server

import com.google.gson.JsonElement
import com.routesme.taxi_screen.kotlin.Model.*
import retrofit2.Call
import retrofit2.http.*

interface RoutesApi {

    //Authentication (Token)...
    @POST("auth")
    @Headers("Content-Type: application/json", "DataServiceVersion: 2.0")
    fun loginAuth(@Body authCredentials: AuthCredentials?): Call<JsonElement>


    //Tablet registration...
    @POST("Tablets")
    @Headers("Content-Type: application/json", "DataServiceVersion: 2.0")
    fun tabletRegister(@Body tabletCredentials: TabletCredentials?): Call<TabletInfo?>?

    @GET("TaxiOffices")
    fun getTaxiOfficeList(@Query("include") include: String?): Call<TaxiOfficeList?>?

    @GET("Tablets")
    fun getOfficePlatesList(@Query("taxiOfficeId") taxiOfficeId: Int): Call<OfficePlatesList?>?


    //Advertisements...
    @GET("Channels")
    fun getVideos(@Query("channelidvideolist") ch_ID_Videos: Int): Call<List<VideoModel>>

    @GET("Channels")
    fun getBanners(@Query("channelidadvlist") ch_ID_Banners: Int): Call<List<BannerModel>>


}