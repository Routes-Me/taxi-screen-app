package com.routesme.taxi_screen.MVVM.Repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi_screen.MVVM.API.RestApiService
import com.routesme.taxi_screen.MVVM.Model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationRepository(val context: Context) {
    private val registrationResponse = MutableLiveData<RegistrationResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }

    fun register(registrationCredentials: RegistrationCredentials): MutableLiveData<RegistrationResponse> {
        val call = thisApiCorService.register(registrationCredentials)
        registrationCredentials
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val registrationSuccessResponse = Gson().fromJson<RegistrationSuccessResponse>(response.body(), RegistrationSuccessResponse::class.java)
                    registrationResponse.value = RegistrationResponse(deviceId = registrationSuccessResponse.deviceId)
                } else{
                    if (response.body() != null){
                        val errors = Gson().fromJson<ResponseErrors>(response.body(), ResponseErrors::class.java)
                        registrationResponse.value = RegistrationResponse(mResponseErrors = errors)
                    }else{
                        val error = Error(detail = response.message(),status = response.code())
                        val errors = mutableListOf<Error>().apply { add(error)  }.toList()

                        val responseErrors = ResponseErrors(errors)
                        registrationResponse.value = RegistrationResponse(mResponseErrors = responseErrors)
                    }
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                registrationResponse.value = RegistrationResponse(mThrowable = throwable)
            }
        })
        return registrationResponse
    }
}