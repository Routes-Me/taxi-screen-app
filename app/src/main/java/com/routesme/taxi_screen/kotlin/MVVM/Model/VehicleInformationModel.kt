package com.routesme.taxi_screen.kotlin.MVVM.Model

import com.google.gson.annotations.SerializedName
import com.routesme.taxi_screen.kotlin.Model.Pagination
import com.routesme.taxi_screen.kotlin.Model.Response
import com.routesme.taxi_screen.kotlin.Model.ResponseErrors

class VehicleInformationModel {

    enum class VehicleInformationListType(val value: String) { Institution("Institution"), Vehicle("Vehicle") }

    data class Institutions(val pagination: Pagination? = null, @SerializedName("data") val data: List<InstitutionData>, val message: String? = null, val status: Boolean = false, val responseCode: Int = -999)
    data class InstitutionData(val createdAt: String? = null, val phoneNumber: String? = null, val institutionId: Int = -999, val countryIso: String? = null, val name: String? = null)

    data class Vehicles(val pagination: Pagination? = null, @SerializedName("data") val data: List<VehicleData>, val message: String? = null, val status: Boolean = false, val responseCode: Int = -999)
    data class VehicleData(val institutionId: Int = -999, val modelId: Int = -999, val vehicleId: Int = -999, val plateNumber: String? = null, val modelYear: Int = -999)

    class InstitutionsResponse(data: List<InstitutionData>? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

        var data: List<InstitutionData>? = null
        init {
            this.data = data
        }

        val isSuccess: Boolean
            get() = (mResponseErrors == null && mThrowable == null)
    }

    class VehiclesResponse(data: List<VehicleData>? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

        var data: List<VehicleData>? = null
        init {
            this.data = data
        }

        val isSuccess: Boolean
            get() = (mResponseErrors == null && mThrowable == null)
    }
}