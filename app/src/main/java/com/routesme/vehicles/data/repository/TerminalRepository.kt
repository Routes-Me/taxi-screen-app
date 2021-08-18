package com.routesme.vehicles.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.model.TerminalResponse
import com.routesme.vehicles.data.model.TerminalCredentials
import com.google.gson.JsonElement
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import com.google.gson.Gson
import com.routesme.vehicles.data.model.*
import org.json.JSONObject
import java.net.HttpURLConnection

class TerminalRepository(val context: Context) {
    private val terminalResponse = MutableLiveData<TerminalResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }

    fun register(terminalCredentials: TerminalCredentials): MutableLiveData<TerminalResponse> {
        val call = thisApiCorService.registerTerminal(terminalCredentials)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val registerTerminalSuccessResponse = Gson().fromJson<RegisterTerminalSuccessResponse>(response.body(), RegisterTerminalSuccessResponse::class.java)
                    terminalResponse.value = TerminalResponse(terminalId = registerTerminalSuccessResponse.terminalId)
                } else {
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_CONFLICT) {
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        terminalResponse.value = TerminalResponse(mResponseErrors = errors)
                    } else {
                        val error = Error(detail = response.message(), statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error) }.toList()
                        val responseErrors = ResponseErrors(errors)
                        terminalResponse.value = TerminalResponse(mResponseErrors = responseErrors)
                    }
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                terminalResponse.value = TerminalResponse(mThrowable = throwable)
            }
        })
        return terminalResponse
    }

    fun update(terminalId: String, terminalCredentials: TerminalCredentials): MutableLiveData<TerminalResponse> {
        val call = thisApiCorService.updateTerminal(terminalId, terminalCredentials)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    terminalResponse.value = TerminalResponse(terminalId = null)
                } else {
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_CONFLICT) {
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        terminalResponse.value = TerminalResponse(mResponseErrors = errors)
                    } else {
                        val error = Error(detail = response.message(), statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error) }.toList()
                        val responseErrors = ResponseErrors(errors)
                        terminalResponse.value = TerminalResponse(mResponseErrors = responseErrors)
                    }
                }
            }
            override fun onFailure(call: Call<Void>, throwable: Throwable) {
                terminalResponse.value = TerminalResponse(mThrowable = throwable)
            }
        })
        return terminalResponse
    }
}