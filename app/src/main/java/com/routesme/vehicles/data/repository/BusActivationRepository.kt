package com.routesme.vehicles.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.model.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

class BusActivationRepository(val context: Context) {
    private val activateBusResponse = MutableLiveData<ActivateBusResponse>()
    private val deactivateBusResponse = MutableLiveData<DeactivateBusResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createNewCorService(context)
    }

    fun activate(busActivationCredentials: BusActivationCredentials): MutableLiveData<ActivateBusResponse> {
        val call = thisApiCorService.activateBus(busActivationCredentials)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val activateBusSuccessResponse = Gson().fromJson<ActivateBusSuccessResponse>(response.body(), ActivateBusSuccessResponse::class.java)
                    activateBusResponse.value = ActivateBusResponse(activateBusDescription = activateBusSuccessResponse.activateBusDescription)
                } else {
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_CONFLICT) {
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        activateBusResponse.value = ActivateBusResponse(mResponseErrors = errors)
                    } else {
                        val error = Error(detail = response.message(), statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error) }.toList()
                        val responseErrors = ResponseErrors(errors)
                        activateBusResponse.value = ActivateBusResponse(mResponseErrors = responseErrors)
                    }
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                activateBusResponse.value = ActivateBusResponse(mThrowable = throwable)
            }
        })
        return activateBusResponse
    }

    fun deactivate(busActivationCredentials: BusActivationCredentials): MutableLiveData<DeactivateBusResponse> {
        val call = thisApiCorService.deactivateBus(busActivationCredentials)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val deactivateBusSuccessResponse = Gson().fromJson<DeactivateBusSuccessResponse>(response.body(), DeactivateBusSuccessResponse::class.java)
                    deactivateBusResponse.value = DeactivateBusResponse(deactivateBusDescription = deactivateBusSuccessResponse.deactivateBusDescription)
                } else {
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_CONFLICT) {
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        deactivateBusResponse.value = DeactivateBusResponse(mResponseErrors = errors)
                    } else {
                        val error = Error(detail = response.message(), statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error) }.toList()
                        val responseErrors = ResponseErrors(errors)
                        deactivateBusResponse.value = DeactivateBusResponse(mResponseErrors = responseErrors)
                    }
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                deactivateBusResponse.value = DeactivateBusResponse(mThrowable = throwable)
            }
        })
        return deactivateBusResponse
    }
}