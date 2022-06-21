package com.routesme.vehicles.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.model.Content
import com.routesme.vehicles.data.model.ContentResponse
import com.routesme.vehicles.data.model.Error
import com.routesme.vehicles.data.model.ResponseErrors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentRepository(val context: Context) {

    private val contentResponse = MutableLiveData<ContentResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createOldCorService(context)
    }
    fun getContent(offset: Int, limit: Int, institutionId: String, vehicleId: String, plateNumber: String): MutableLiveData<ContentResponse> {
        val call = thisApiCorService.getContent(offset, limit, institutionId, vehicleId, plateNumber)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val content = Gson().fromJson<Content>(response.body(), Content::class.java)
                    contentResponse.value = ContentResponse(data = content.data)
                } else{
                    val error = Error(detail = response.message(), statusCode = response.code())
                    val errors = mutableListOf<Error>().apply { add(error)  }.toList()
                    val responseErrors = ResponseErrors(errors)
                    contentResponse.value = ContentResponse(mResponseErrors = responseErrors)
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                contentResponse.value = ContentResponse(mThrowable = throwable)
            }
        })
        return contentResponse
    }
}