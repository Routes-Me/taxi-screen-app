package com.routesme.vehicles.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.model.BusInformationModel.*
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import com.google.gson.Gson
import com.routesme.vehicles.data.model.Error
import com.routesme.vehicles.data.model.ResponseErrors

class BusInformationRepository(val context: Context){

    private val busInformationResponse = MutableLiveData<BusInformationResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }

    fun getBusInformation(vehicleId: String, include: String): MutableLiveData<BusInformationResponse> {
        val call = thisApiCorService.getBusInformation(vehicleId, include)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val busInformation = Gson().fromJson<BusInformationModel>(response.body(), BusInformationModel::class.java)
                    busInformationResponse.value = BusInformationResponse(busInformationModel = busInformation)
                } else {
                    val error = Error(detail = response.message(), statusCode = response.code())
                    val errors = mutableListOf<Error>().apply { add(error) }.toList()
                    val responseErrors = ResponseErrors(errors)
                    busInformationResponse.value = BusInformationResponse(mResponseErrors = responseErrors)
                }
            }

            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                busInformationResponse.value = BusInformationResponse(mThrowable = throwable)
            }
        })
        return busInformationResponse
    }
}