package com.routesme.vehicles.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.model.Error
import com.routesme.vehicles.data.model.ReportResponse
import com.routesme.vehicles.data.model.ResponseErrors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportRepository(context: Context, data: JsonArray) {

    private val reportResponse = MutableLiveData<ReportResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createOldCorService(context)
    }

    fun postReport(data: JsonArray, deviceId: String): MutableLiveData<ReportResponse> {
        val call = thisApiCorService.postReport(data, deviceId)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful) {
                    reportResponse.value = ReportResponse()
                } else {
                    val error = Error(detail = response.message(), statusCode = response.code())
                    val errors = mutableListOf<Error>().apply { add(error) }.toList()
                    val responseErrors = ResponseErrors(errors)
                    reportResponse.value = ReportResponse(mResponseErrors = responseErrors)
                }
            }

            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                reportResponse.value = ReportResponse(mThrowable = throwable)
            }
        })
        return reportResponse
    }

}