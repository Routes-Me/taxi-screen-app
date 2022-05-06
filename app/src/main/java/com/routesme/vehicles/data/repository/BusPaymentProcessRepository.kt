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

class BusPaymentProcessRepository(val context: Context) {
    private val busPaymentProcessResponse = MutableLiveData<BusPaymentProcessResponse>()

    private val thisApiCorService by lazy { RestApiService.createNewCorService(context) }

    fun processed(busPaymentProcessCredentials: BusPaymentProcessCredentials): MutableLiveData<BusPaymentProcessResponse> {
        val call = thisApiCorService.busPaymentProcess(busPaymentProcessCredentials)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val busPaymentProcessDTO = Gson().fromJson<BusPaymentProcessDTO>(response.body(), BusPaymentProcessDTO::class.java)
                    busPaymentProcessResponse.value =
                            if (busPaymentProcessDTO.status) BusPaymentProcessResponse(isProcessedSuccessfully = busPaymentProcessDTO.status, busPaymentProcessSuccessDTO = Gson().fromJson<BusPaymentProcessSuccessDTO>(response.body()!!.asJsonObject["description"], BusPaymentProcessSuccessDTO::class.java))
                            else BusPaymentProcessResponse(isProcessedSuccessfully = busPaymentProcessDTO.status, busPaymentProcessFailedDTO = Gson().fromJson<String>(response.body()!!.asJsonObject["description"], String::class.java))
                } else {
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_CONFLICT) {
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        busPaymentProcessResponse.value = BusPaymentProcessResponse(mResponseErrors = errors)
                    } else {
                        val error = Error(detail = response.message(), statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error) }.toList()
                        val responseErrors = ResponseErrors(errors)
                        busPaymentProcessResponse.value = BusPaymentProcessResponse(mResponseErrors = responseErrors)
                    }
                }
            }

            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                busPaymentProcessResponse.value = BusPaymentProcessResponse(mThrowable = throwable)
            }
        })
        return busPaymentProcessResponse
    }
}