package com.routesme.taxi_screen.kotlin.Server

import com.google.gson.JsonElement
import com.routesme.taxi_screen.kotlin.Model.*
import retrofit2.Call
import retrofit2.http.*

interface RoutesApi {

    //Authentication (Token)...
    @POST("auth")
    @Headers("Content-Type: application/json", "DataServiceVersion: 2.0")
    fun loginAuth(@Body authCredentials: AuthCredentials?): Call<JsonElement?>?


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
  //  @Headers("Authorization: Bearer mEh1qi1vanNWx1fva1kYQfL0PJL9FOrrlf5fXQ85Naie8dsoUNnbWMN1yaLnWD1SJxgxbzN1zoCxXcJTVFs1cYf2NzfHH2p6lg_rrnPJqqjG3uDfNMgM4HKFqvGnqkYc6qDIKjct7KgAh_yT2jGw-gDl66DmdBmaWcVEoAXVZkr_VjSYtJOn4S3iS2LmoZ07S05fBmTkYHCmbzoOzLQJctBpNE5WCi7GE2nR7NCOopYzpf0SMELgOin_1RdYNL7i8eM99Q4MH4I3ecbiPijlt64CJzYMZ_UfECkwHohKSQKTtiiXek7UFb_uhF9jw_yQzWZfW1XUgbBUedJA_rnxoVG1eezTR-BDbkTkqHd4dQGTeyMM04LzIteISkJUnywRPVCT2dtekmCRlVWydO7svceVbNERSKikdjbSKCb5uFWHal8xF_0nj53ol3dUL_2QsHKKGr3ghGX2Q12z-9XRA5iRI7w5UKPNET-xngUMhffHh_S41ZVLXCGwrz1wAK5H")
    fun getVideos(@Query("channelidvideolist") ch_ID_Videos: Int): Call<List<VideoModel?>?>?

    @GET("Channels")
    fun getBanners(@Query("channelidadvlist") ch_ID_Banners: Int): Call<List<BannerModel?>?>?


}