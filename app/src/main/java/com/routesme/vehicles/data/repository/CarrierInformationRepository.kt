package com.routesme.vehicles.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.model.CarrierInformationModel.*
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import com.google.gson.Gson
import com.routesme.vehicles.data.model.Error
import com.routesme.vehicles.data.model.ResponseErrors

class CarrierInformationRepository(val context: Context){

    private val carrierInformationResponse = MutableLiveData<CarrierInformationResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createOldCorService(context)
    }

    fun getCarrierInformation(vehicleId: String, include: String): MutableLiveData<CarrierInformationResponse> {
        val call = thisApiCorService.getCarrierInformation(vehicleId, include)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val carrierInformation = Gson().fromJson<CarrierInformationModel>(response.body(), CarrierInformationModel::class.java)
                    carrierInformationResponse.value = CarrierInformationResponse(carrierInformationModel = carrierInformation)
                } else {
                    val error = Error(detail = response.message(), statusCode = response.code())
                    val errors = mutableListOf<Error>().apply { add(error) }.toList()
                    val responseErrors = ResponseErrors(errors)
                    carrierInformationResponse.value = CarrierInformationResponse(mResponseErrors = responseErrors)
                }
            }

            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                carrierInformationResponse.value = CarrierInformationResponse(mThrowable = throwable)
            }
        })
        return carrierInformationResponse
    }
}