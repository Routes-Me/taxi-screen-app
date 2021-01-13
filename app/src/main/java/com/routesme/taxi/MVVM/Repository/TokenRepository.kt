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

class TokenRepository(context: Context,refresh_token:String?){
    private val tokenResponse = MutableLiveData<RefreshResponse>()
    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }
    fun refreshToken(refresh_token: String?): MutableLiveData<RefreshResponse> {

        val call = thisApiCorService.refreshToken(refresh_token)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful) {

                    val refreshModel = Gson().fromJson<RefreshModel>(response.body(), RefreshModel::class.java::class.java)
                    tokenResponse.value = RefreshResponse(token = refreshModel.token,refresh_token = refreshModel.refresh_token)

                } else{
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_UNAUTHORIZED){
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        tokenResponse.value = RefreshResponse(mResponseErrors = errors)
                    }else{
                        val error = Error(detail = response.message(),statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error)  }.toList()
                        val responseErrors = ResponseErrors(errors)
                        tokenResponse.value = RefreshResponse(mResponseErrors = responseErrors)
                    }
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                tokenResponse.value = RefreshResponse(mThrowable = throwable)
            }
        })
        return tokenResponse
    }

}