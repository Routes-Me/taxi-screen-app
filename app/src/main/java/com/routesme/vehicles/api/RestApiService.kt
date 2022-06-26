package com.routesme.vehicles.api

import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.routesme.vehicles.BuildConfig
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.room.entity.LocationCoordinate
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
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
    fun getContent(@Query("offset") offset: Int, @Query("limit") limit: Int , @Query("institutionId") institutionId: String, @Query("vehicleId") vehicleId: String, @Query("plateNumber") plateNumber: String): Call<JsonElement>

    @POST("analytics/devices/{deviceId}/playbacks")
    fun postReport(@Body data: JsonArray, @Path("deviceId") deviceId: String): Call<JsonElement>

    @POST("authentications/renewals")
    fun refreshToken(@Body refreshTokenCredentials: RefreshTokenCredentials): Call<JsonElement>

    @DELETE("vehicles/{vehilceId}/devices/{deviceId}")
    fun deleteVehicle(@Path("vehilceId") vehilceId:String,@Path("deviceId") deviceId:String): Call<JsonElement>

    @GET("carriers/{vehicleId}")
    fun getCarrierInformation(@Path("vehicleId") vehicleId: String, @Query("include") include: String): Call<JsonElement>

    @POST("vehicles/{vehicleId}/coordinates")
    fun locationCoordinates (@Path("vehicleId") vehicleId: String, @Body coordinates: List<LocationCoordinate>): Call<String>



    //New server endpoints [Bus App]

    @POST("divice/ActiveBus")
    fun activateBus(@Body busActivationCredentials: BusActivationCredentials): Call<JsonElement>

    @POST("divice/UnActiveBus")
    fun deactivateBus(@Body busActivationCredentials: BusActivationCredentials): Call<JsonElement>

    @POST("divice/PaymentBySecondID")
    fun busPaymentProcess(@Body busPaymentProcessCredentials: BusPaymentProcessCredentials): Call<JsonElement>

    companion object {

        fun createOldCorService(context: Context): RestApiService {
            return Retrofit.Builder()
                    .baseUrl(BuildConfig.OLD_STAGING_BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(ApiWorker(context).gsonConverter!!)
                    .client(ApiWorker(context).client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build().create(RestApiService::class.java)
        }

        fun createNewCorService(context: Context): RestApiService {
            return Retrofit.Builder()
                    .baseUrl(BuildConfig.NEW_PRODUCTION_BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(ApiWorker(context).gsonConverter!!)
                    .client(ApiWorker(context).client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build().create(RestApiService::class.java)
        }
    }
}