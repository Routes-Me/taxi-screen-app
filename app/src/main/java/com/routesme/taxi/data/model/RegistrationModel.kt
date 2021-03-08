package com.routesme.taxi.data.model

import com.routesme.taxi.data.model.Response
import com.routesme.taxi.data.model.ResponseErrors

data class RegistrationCredentials(var serialNumber: String? = null, var `SimSerialNumber`: String? = null, var VehicleId: String? = null)
data class RegistrationSuccessResponse(val deviceId: String? = null, val status: Boolean = false, val message: String? = null, val statusCode: Int = -999)
class RegistrationResponse(deviceId: String? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    var deviceId: String? = null
    init {

       this.deviceId = deviceId


    }

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)





}