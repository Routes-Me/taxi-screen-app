package com.routesme.taxi.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi.api.RestApiService
import com.routesme.taxi.data.model.Content
import com.routesme.taxi.data.model.ContentResponse
import com.routesme.taxi.data.model.Error
import com.routesme.taxi.data.model.ResponseErrors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentRepository(val context: Context) {

    private val contentResponse = MutableLiveData<ContentResponse>()

    private val thisApiCoreService by lazy {
        RestApiService.createCorService(context)
    }
    fun getContent(offset: Int, limit: Int): MutableLiveData<ContentResponse> {
        val call = thisApiCoreService.getContent(offset,limit)
        Log.d("RefreshToken", "ContentRepository..Call: $call")
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