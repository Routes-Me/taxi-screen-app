package com.routesme.taxi_screen.kotlin.MVVM.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi_screen.kotlin.Class.AesBase64Wrapper
import com.routesme.taxi_screen.kotlin.MVVM.API.RestApiService
import com.routesme.taxi_screen.kotlin.MVVM.Model.SignInCredentials
import com.routesme.taxi_screen.kotlin.MVVM.Model.LoginResponse
import com.routesme.taxi_screen.kotlin.MVVM.Model.SignInSuccessResponse
import com.routesme.taxi_screen.kotlin.Model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository(val context: Context) {
    private val signInResponse = MutableLiveData<LoginResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }

    fun signIn(signInCredentials: SignInCredentials): MutableLiveData<LoginResponse> {
        val encryptedPassword = encrypt(signInCredentials.Password)
        Log.d("Encryption", encryptedPassword)

        val encryptedCredentials = SignInCredentials(signInCredentials.Username, encryptedPassword)
        val call = thisApiCorService.signIn(encryptedCredentials)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val signInSuccessResponse = Gson().fromJson<SignInSuccessResponse>(response.body(), SignInSuccessResponse::class.java)
                    signInResponse.value = LoginResponse(token = signInSuccessResponse.token)
                } else{
                    if (response.body() != null){
                        val errors = Gson().fromJson<ResponseErrors>(response.body(), ResponseErrors::class.java)
                        signInResponse.value = LoginResponse(mResponseErrors = errors)
                    }else{
                        val error = Error(detail = response.message(),status = response.code())
                        val errors = mutableListOf<Error>().apply { add(error)  }.toList()

                        val responseErrors = ResponseErrors(errors)
                        signInResponse.value = LoginResponse(mResponseErrors = responseErrors)
                    }
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                signInResponse.value = LoginResponse(mThrowable = throwable)
            }
        })
        return signInResponse
    }

    private fun encrypt(str: String) = AesBase64Wrapper().getEncryptedString(str)
}