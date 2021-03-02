package com.routesme.taxi.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi.api.RestApiService
import com.routesme.taxi.data.model.*
import com.routesme.taxi.uplevels.Account
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenRepository(val context: Context) {
    private val refreshTokenResponse = MutableLiveData<RefreshTokenResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }

    fun refreshToken(): MutableLiveData<RefreshTokenResponse> {
        Log.d("Retry-Count", "Hit Refresh Token")
        val refreshTokenCredentials = RefreshTokenCredentials(Account().refreshToken.toString())
        val call = thisApiCorService.refreshToken(refreshTokenCredentials)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {

                if (response.isSuccessful && response.body() != null) {
                    val successResponse = Gson().fromJson<RefreshTokenSuccessResponse>(response.body(), RefreshTokenSuccessResponse::class.java)

                   // refreshTokenResponse.value = RefreshTokenResponse(accessToken = refreshTokenSuccessResponse.accessToken, refreshToken = refreshTokenSuccessResponse.refreshToken)
                    refreshTokenResponse.value = RefreshTokenResponse(accessToken = successResponse.accessToken, refreshToken = successResponse.refreshToken)
                }
                /*
                else{
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE){
                        //logout()
                    }
                    /*
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_UNAUTHORIZED){
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        refreshTokenResponse.value = RefreshTokenResponse(mResponseErrors = errors)
                    }else{
                        val error = Error(detail = response.message(), statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error)  }.toList()
                        val responseErrors = ResponseErrors(errors)
                        refreshTokenResponse.value = RefreshTokenResponse(mResponseErrors = responseErrors)
                    }
                    */
                }
                */
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                refreshTokenResponse.value = RefreshTokenResponse(mThrowable = throwable)
            }
        })
        return refreshTokenResponse
    }
}