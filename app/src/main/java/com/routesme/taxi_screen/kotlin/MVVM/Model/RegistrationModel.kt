package com.routesme.taxi_screen.kotlin.MVVM.Model

import com.google.gson.annotations.SerializedName
import com.routesme.taxi_screen.kotlin.Model.Pagination
import com.routesme.taxi_screen.kotlin.Model.Response
import com.routesme.taxi_screen.kotlin.Model.ResponseErrors

enum class VehicleInformationListType(val value: String) { Institution("Institution"), Vehicle("Vehicle") }
data class RegistrationCredentials(var DeviceSerialNumber: String? = null, var SimSerialNumber: String? = null, var VehicleId: Int = -999)
data class RegistrationSuccessResponse(val deviceId: Int = -999, val status: Boolean = false, val message: String? = null, val responseCode: Int = -999)

data class Institutions(val pagination: Pagination? = null, @SerializedName("data") val data: List<InstitutionData>, val message: String? = null, val status: Boolean = false, val responseCode: Int = -999)
data class InstitutionData(val createdAt: String? = null, val phoneNumber: String? = null, val institutionId: Int = -999, val countryIso: String? = null, val name: String? = null)

data class Vehicles(val pagination: Pagination? = null, @SerializedName("data") val data: List<VehicleData>, val message: String? = null, val status: Boolean = false, val responseCode: Int = -999)
data class VehicleData(val institutionId: Int = -999, val modelId: Int = -999, val vehicleId: Int = -999, val plateNumber: String? = null, val modelYear: Int = -999)

class RegistrationResponse(deviceId: Int? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

     var deviceId: Int? = null
    init {
        this.deviceId = deviceId
    }

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}