package com.routesme.taxi.data.model

import com.google.gson.annotations.SerializedName

class VehicleInformationModel {

    data class Item(val id: String? = null, val itemName: String? = null, val isHeader: Boolean = false)

    enum class VehicleInformationListType(val value: String) { Institution("Institution"), Vehicle("Vehicle") }

    data class Institutions(val pagination: Pagination? = null, val data: List<InstitutionData>, val message: String? = null, val status: Boolean = false, val statusCode: Int = -999)
    data class InstitutionData( val institutionId: String? = null, val name: String? = null)

    data class Vehicles(val pagination: Pagination? = null, @SerializedName("data") val data: List<VehicleData>, val message: String? = null, val status: Boolean = false, val statusCode: Int = -999)
    data class VehicleData(val vehicleId: String? = null, val plateNumber: String? = null)

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