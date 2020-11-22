package com.routesme.taxi.MVVM.Repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi.MVVM.API.RestApiService
import com.routesme.taxi.MVVM.Model.*
import org.json.JSONObject
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
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val submitApplicationVersionSuccessResponse = Gson().fromJson<SubmitApplicationVersionSuccessResponse>(response.body(), SubmitApplicationVersionSuccessResponse::class.java)
                    submitApplicationVersionResponse.value = SubmitApplicationVersionResponse(submitApplicationVersionSuccessResponse = submitApplicationVersionSuccessResponse)
                } else{
                    if (response.errorBody() != null){
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        submitApplicationVersionResponse.value = SubmitApplicationVersionResponse(mResponseErrors = errors)
                    }else{
                        val error = Error(detail = response.message(),statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error)  }.toList()
                        val responseErrors = ResponseErrors(errors)
                        submitApplicationVersionResponse.value = SubmitApplicationVersionResponse(mResponseErrors = responseErrors)
                    }
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                submitApplicationVersionResponse.value = SubmitApplicationVersionResponse(mThrowable = throwable)
            }
        })
        return submitApplicationVersionResponse
    }
}