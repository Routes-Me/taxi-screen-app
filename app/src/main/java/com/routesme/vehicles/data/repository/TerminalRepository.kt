package com.routesme.vehicles.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TerminalRepository(val context: Context){

    private val terminalResponse = MutableLiveData<TerminalResponse>()
    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }
    fun createTerminal(parameter: Parameter): MutableLiveData<TerminalResponse> {
        val call = thisApiCorService.createTerminal(parameter)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val terminal = Gson().fromJson<TerminalSuccessResponse>(response.body(), TerminalSuccessResponse::class.java)
                    terminalResponse.value = TerminalResponse(terminalId = terminal.terminalId)
                } else {
                    val error = Error(detail = response.message(), statusCode = response.code())
                    val errors = mutableListOf<Error>().apply { add(error) }.toList()
                    val responseErrors = ResponseErrors(errors)
                    terminalResponse.value = TerminalResponse(mResponseErrors = responseErrors)
                }
            }

            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                terminalResponse.value = TerminalResponse(mThrowable = throwable)
            }
        })
        return terminalResponse
    }
}