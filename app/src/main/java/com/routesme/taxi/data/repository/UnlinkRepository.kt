package com.routesme.taxi.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi.api.RestApiService
import com.routesme.taxi.data.model.Error
import com.routesme.taxi.data.model.ResponseErrors
import com.routesme.taxi.data.model.UnlinkResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

class UnlinkRepository(context: Context) {
    private val unlinkResponse = MutableLiveData<UnlinkResponse>()
    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }

    fun unlink(vehicleId: String, deviceId: String): MutableLiveData<UnlinkResponse> {

        val call = thisApiCorService.deleteVehicle(vehicleId, deviceId)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {

                if (response.isSuccessful) {
                    unlinkResponse.value = UnlinkResponse()

                } else {
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        unlinkResponse.value = UnlinkResponse(mResponseErrors = errors)
                    } else {
                        val error = Error(detail = response.message(), statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error) }.toList()
                        val responseErrors = ResponseErrors(errors)
                        unlinkResponse.value = UnlinkResponse(mResponseErrors = responseErrors)
                    }
                }
            }

            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                unlinkResponse.value = UnlinkResponse(mThrowable = throwable)
            }
        })
        return unlinkResponse
    }

}