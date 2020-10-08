package com.routesme.taxi_screen.MVVM.Repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi_screen.MVVM.API.RestApiService
import com.routesme.taxi_screen.MVVM.Model.Error
import com.routesme.taxi_screen.MVVM.Model.ResponseErrors
import com.routesme.taxi_screen.MVVM.Model.VehicleInformationModel.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleInformationRepository(val context: Context) {

    private val institutionsResponse = MutableLiveData<InstitutionsResponse>()
    private val vehiclesResponse = MutableLiveData<VehiclesResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }

    fun getInstitutions(offset: Int, limit: Int): MutableLiveData<InstitutionsResponse> {
        val call = thisApiCorService.getInstitutions(offset,limit)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val institutions = Gson().fromJson<Institutions>(response.body(), Institutions::class.java)
                    institutionsResponse.value = InstitutionsResponse(data = institutions.data)
                } else{
                    if (response.errorBody() != null){
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        institutionsResponse.value = InstitutionsResponse(mResponseErrors = errors)
                    }else{
                        val error = Error(detail = response.message(),statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error)  }.toList()
                        val responseErrors = ResponseErrors(errors)
                        institutionsResponse.value = InstitutionsResponse(mResponseErrors = responseErrors)
                    }
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                institutionsResponse.value = InstitutionsResponse(mThrowable = throwable )
            }
        })
        return institutionsResponse
    }

    fun getVehicles(institutionId: String, offset: Int, limit: Int): MutableLiveData<VehiclesResponse> {
        val call = thisApiCorService.getVehicles(institutionId,offset,limit)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val vehicles = Gson().fromJson<Vehicles>(response.body(), Vehicles::class.java)
                    vehiclesResponse.value = VehiclesResponse(data = vehicles.data)
                } else{
                    if (response.errorBody() != null){
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        vehiclesResponse.value = VehiclesResponse(mResponseErrors = errors)
                    }else{
                        val error = Error(detail = response.message(),statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error)  }.toList()
                        val responseErrors = ResponseErrors(errors)
                        vehiclesResponse.value = VehiclesResponse(mResponseErrors = responseErrors)
                    }
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                vehiclesResponse.value = VehiclesResponse(mThrowable = throwable)
            }
        })
        return vehiclesResponse
    }
}