package com.routesme.taxi.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.routesme.taxi.api.RestApiService
import com.routesme.taxi.data.model.Error
import com.routesme.taxi.data.model.ResponseErrors
import com.routesme.taxi.data.model.SubmitApplicationVersionCredentials
import com.routesme.taxi.data.model.SubmitApplicationVersionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubmitApplicationVersionRepository(val context: Context) {
    private val submitApplicationVersionResponse = MutableLiveData<SubmitApplicationVersionResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }

    fun submitApplicationVersion(deviceId: String, submitApplicationVersionCredentials: SubmitApplicationVersionCredentials): MutableLiveData<SubmitApplicationVersionResponse> {
        val call = thisApiCorService.submitApplicationVersion(deviceId, submitApplicationVersionCredentials)
        Log.d("RefreshToken", "SubmitApplicationVersionRepository..Call: $call")
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                //Log.d("Submit Version","${response.code()},${response.message()},${call.request().url()}")
                if (response.isSuccessful) {
                    submitApplicationVersionResponse.value = SubmitApplicationVersionResponse(isSuccess = true)
                } else {
                    val error = Error(detail = response.message(), statusCode = response.code())
                    val errors = mutableListOf<Error>().apply { add(error) }.toList()
                    val responseErrors = ResponseErrors(errors)
                    submitApplicationVersionResponse.value = SubmitApplicationVersionResponse(mResponseErrors = responseErrors)
                }
            }

            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                submitApplicationVersionResponse.value = SubmitApplicationVersionResponse(mThrowable = throwable)
            }
        })
        return submitApplicationVersionResponse
    }
}