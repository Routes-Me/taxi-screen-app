package com.routesme.vehicles.api

import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.routesme.vehicles.helper.Helper
import com.routesme.vehicles.data.model.RegistrationCredentials
import com.routesme.vehicles.data.model.SignInCredentials
import com.routesme.vehicles.data.model.SubmitApplicationVersionCredentials
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.RefreshTokenCredentials
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*

interface RestApiService {

    @PUT("devices/{id}/applications")
    fun submitApplicationVersion(@Path("id") deviceId: String, @Body submitApplicationVersionCredentials: SubmitApplicationVersionCredentials): Call<JsonElement>

    //@POST("signin")
    @POST("authentications")
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

    @POST("authentications/renewals")
    fun refreshToken(@Body refreshTokenCredentials: RefreshTokenCredentials): Call<JsonElement>

    @DELETE("vehicles/{vehilceId}/devices/{deviceId}")
    fun deleteVehicle(@Path("vehilceId") vehilceId:String,@Path("deviceId") deviceId:String): Call<JsonElement>

    @GET("buses/{vehicleId}")
    fun getBusInformation(@Path("vehicleId") vehicleId: String, @Query("include") include: String): Call<JsonElement>

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