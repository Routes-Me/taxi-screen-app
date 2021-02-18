package com.routesme.taxi.MVVM.Repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.routesme.taxi.MVVM.API.RestApiService
import com.routesme.taxi.MVVM.Model.Error
import com.routesme.taxi.MVVM.Model.Report
import com.routesme.taxi.MVVM.Model.ReportResponse
import com.routesme.taxi.MVVM.Model.ResponseErrors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportRepository(context:Context,data: JsonArray){

    private val reportResponse = MutableLiveData<ReportResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }
    fun postReport(data: JsonArray,deviceId:String): MutableLiveData<ReportResponse> {
        //Log.d("Report","${deviceId},${data}")
        val call = thisApiCorService.postReport(data,deviceId)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                //Log.d("Report","${response.code()},${response.message()},${call.request().url()}")
                if(response.code() == 201){
                    val content = Gson().fromJson<Report>(response.body(), Report::class.java)
                    reportResponse.value = ReportResponse(content.statusCode)

                }else{
                    val error = Error(detail = response.message(),statusCode = response.code())
                    val errors = mutableListOf<Error>().apply { add(error)  }.toList()
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