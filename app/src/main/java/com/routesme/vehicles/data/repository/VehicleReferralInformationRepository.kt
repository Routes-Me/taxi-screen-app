package com.routesme.vehicles.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.model.Error
import com.routesme.vehicles.data.model.ResponseErrors
import com.routesme.vehicles.data.model.VehicleReferralInformationModel.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleReferralInformationRepository (val context: Context) {
    private val vehicleReferralResponse = MutableLiveData<VehicleReferralResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createOldCorService(context)
    }

    fun getVehicleReferral(vehicleId: String): MutableLiveData<VehicleReferralResponse> {
        val call = thisApiCorService.getVehicleReferralInformation(vehicleId)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d("GetVehicleReferralInformation-API","Response: $response")
                if (response.isSuccessful && response.body() != null) {
                    val vehicles = Gson().fromJson<Vehicles>(response.body(), Vehicles::class.java)
                    vehicleReferralResponse.value =
                        VehicleReferralResponse(data = vehicles.data)
                } else {
                    val error = Error(detail = response.message(), statusCode = response.code())
                    val errors = mutableListOf<Error>().apply { add(error) }.toList()
                    val responseErrors = ResponseErrors(errors)
                    vehicleReferralResponse.value =
                        VehicleReferralResponse(mResponseErrors = responseErrors)
                }
            }

            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                vehicleReferralResponse.value =
                    VehicleReferralResponse(mThrowable = throwable)
            }
        })
        return vehicleReferralResponse
    }
}