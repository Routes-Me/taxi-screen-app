package com.routesme.taxi.MVVM.API

import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.routesme.taxi.Class.Helper
import com.routesme.taxi.MVVM.Model.RegistrationCredentials
import com.routesme.taxi.MVVM.Model.SignInCredentials
import com.routesme.taxi.MVVM.Model.SubmitApplicationVersionCredentials
import com.routesme.taxi.R
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*

interface RestApiService {

    @PUT("devices/{id}/applications")
    fun submitApplicationVersion(@Path("id") deviceId: String, @Body submitApplicationVersionCredentials: SubmitApplicationVersionCredentials): Call<JsonElement>


    @POST("signin")
    fun signIn(@Body signInCredentials: SignInCredentials): Call<JsonElement>

    @POST("devices")
    fun register(@Body registrationCredentials: RegistrationCredentials): Call<JsonElement>

    @GET("institutions")
    fun getInstitutions(@Query("offset") offset: Int, @Query("limit") limit: Int): Call<JsonElement>

    @GET("vehicles")
    fun getVehicles(@Query("institutionId") institutionId: String, @Query("offset") offset: Int, @Query("limit") limit: Int): Call<JsonElement>

    @GET("contents")
    fun getContent(@Query("offset") offset: Int, @Query("limit") limit: Int): Call<JsonElement>

    @POST("analytics/devices/{deviceId}/playbacks")
    fun postReport(@Body data: JsonArray, @Path("deviceId") deviceId: String): Call<JsonElement>

    @POST("analytics/devices/playbacks")
    fun refreshToken(): Call<JsonElement>

    @DELETE("vehicles/{vehilceId}/devices/{deviceId}")
    fun deleteVehicle(@Path("vehilceId") vehilceId:String,@Path("deviceId") deviceId:String): Call<JsonElement>

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