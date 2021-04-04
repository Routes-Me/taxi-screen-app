package com.routesme.vehicles.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.encryption.AesBase64Wrapper
import com.routesme.vehicles.data.model.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

class LoginRepository(val context: Context) {
    private val signInResponse = MutableLiveData<LoginResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }

    fun signIn(signInCredentials: SignInCredentials): MutableLiveData<LoginResponse> {
        val encryptedPassword = encrypt(signInCredentials.password)
        val encryptedCredentials = SignInCredentials(signInCredentials.userName, encryptedPassword)

        val call = thisApiCorService.signIn(encryptedCredentials)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d("authenticationsAPI", "${encryptedCredentials}")
                Log.d("authenticationsAPI", "${response}")
                if (response.isSuccessful && response.body() != null) {
                    val signInSuccessResponse = Gson().fromJson<SignInSuccessResponse>(response.body(), SignInSuccessResponse::class.java)
                    Log.d("authenticationsAPI", "$signInSuccessResponse")
                    signInResponse.value = LoginResponse(token = signInSuccessResponse.token)
                } else {
                    /*
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        signInResponse.value = LoginResponse(mResponseErrors = errors)
                    } else {
                        */
                        val error = Error(detail = response.message(), statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error) }.toList()
                        val responseErrors = ResponseErrors(errors)
                        signInResponse.value = LoginResponse(mResponseErrors = responseErrors)
                   // }
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