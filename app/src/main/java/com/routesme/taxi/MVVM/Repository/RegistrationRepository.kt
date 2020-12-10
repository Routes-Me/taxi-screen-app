package com.routesme.taxi.MVVM.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi.MVVM.API.RestApiService
import com.routesme.taxi.MVVM.Model.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

class RegistrationRepository(val context: Context) {
    private val registrationResponse = MutableLiveData<RegistrationResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }

    fun register(registrationCredentials: RegistrationCredentials): MutableLiveData<RegistrationResponse> {
        val call = thisApiCorService.register(registrationCredentials)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val registrationSuccessResponse = Gson().fromJson<RegistrationSuccessResponse>(response.body(), RegistrationSuccessResponse::class.java)
                    registrationResponse.value = RegistrationResponse(deviceId = registrationSuccessResponse.deviceId)
                } else{
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_CONFLICT){
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        registrationResponse.value = RegistrationResponse(mResponseErrors = errors)
                    }else{
                        val error = Error(detail = response.message(),statusCode = response.code())
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