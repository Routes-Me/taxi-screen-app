package com.routesme.taxi_screen.kotlin.MVVM.Model

import com.routesme.taxi_screen.kotlin.Model.Response
import com.routesme.taxi_screen.kotlin.Model.ResponseErrors

data class RegistrationCredentials(var DeviceSerialNumber: String? = null, var SimSerialNumber: String? = null, var VehicleId: Int = -999)
data class RegistrationSuccessResponse(val deviceId: Int = -999, val status: Boolean = false, val message: String? = null, val responseCode: Int = -999)

class RegistrationResponse(deviceId: Int? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

     var deviceId: Int? = null
    init {
        this.deviceId = deviceId
    }

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}