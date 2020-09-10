package com.routesme.taxi_screen.kotlin.MVVM.Repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi_screen.kotlin.MVVM.API.RestApiService
import com.routesme.taxi_screen.kotlin.MVVM.Model.VehicleInformationModel.*
import com.routesme.taxi_screen.kotlin.Model.ResponseErrors
import com.routesme.taxi_screen.kotlin.Model.Error
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
                    if (response.body() != null){
                        val errors = Gson().fromJson<ResponseErrors>(response.body(), ResponseErrors::class.java)
                        institutionsResponse.value = InstitutionsResponse(mResponseErrors = errors)
                    }else{
                        val error = Error(detail = response.message(),status = response.code())
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

    fun getVehicles(institutionId: Int, offset: Int, limit: Int): MutableLiveData<VehiclesResponse> {
        val call = thisApiCorService.getVehicles(institutionId,offset,limit)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val vehicles = Gson().fromJson<Vehicles>(response.body(), Vehicles::class.java)
                    vehiclesResponse.value = VehiclesResponse(data = vehicles.data)
                } else{
                    if (response.body() != null){
                        val errors = Gson().fromJson<ResponseErrors>(response.body(), ResponseErrors::class.java)
                        vehiclesResponse.value = VehiclesResponse(mResponseErrors = errors)
                    }else{
                        val error = Error(detail = response.message(),status = response.code())
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