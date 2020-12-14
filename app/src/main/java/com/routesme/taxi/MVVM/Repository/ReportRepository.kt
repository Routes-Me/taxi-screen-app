package com.routesme.taxi.MVVM.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi.LocationTrackingService.Model.VideoTracking
import com.routesme.taxi.MVVM.API.RestApiService
import com.routesme.taxi.MVVM.Model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportRepository(context:Context,data:List<VideoTracking>){

    private val reportResponse = MutableLiveData<ReportResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }
    fun postReport(data: List<VideoTracking>): MutableLiveData<ReportResponse> {
        val call = thisApiCorService.postReport(data)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d("Date","Success ${response.code()}")
                if(response.code() == 202){
                    val content = Gson().fromJson<Content>(response.body(), Report::class.java)
                    reportResponse.value = ReportResponse(content.statusCode)

                }else{
                    val error = Error(detail = response.message(),statusCode = response.code())
                    val errors = mutableListOf<Error>().apply { add(error)  }.toList()
                    val responseErrors = ResponseErrors(errors)
                    reportResponse.value = ReportResponse(mResponseErrors = responseErrors)
                }

            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                Log.d("Date","Failure")
                reportResponse.value = ReportResponse(mThrowable = throwable)
            }
        })
        return reportResponse
    }

}