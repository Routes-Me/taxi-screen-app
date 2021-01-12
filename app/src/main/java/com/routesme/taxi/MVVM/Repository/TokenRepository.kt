package com.routesme.taxi.MVVM.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi.MVVM.API.RestApiService
import com.routesme.taxi.MVVM.Model.Error
import com.routesme.taxi.MVVM.Model.RefreshModel
import com.routesme.taxi.MVVM.Model.ResponseErrors
import com.routesme.taxi.MVVM.Model.UnlinkResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

class TokenRepository(context: Context){
    private val tokenResponse = MutableLiveData<RefreshModel>()
    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }
    fun refreshToken(): MutableLiveData<RefreshModel> {

        val call = thisApiCorService.refreshToken()
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful) {




                } else{
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_UNAUTHORIZED){
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        tokenResponse.value = RefreshModel(mResponseErrors = errors)
                    }else{
                        val error = Error(detail = response.message(),statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error)  }.toList()
                        val responseErrors = ResponseErrors(errors)
                        tokenResponse.value = RefreshModel(mResponseErrors = responseErrors)
                    }
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                tokenResponse.value = RefreshModel(mThrowable = throwable)
            }
        })
        return tokenResponse
    }

}