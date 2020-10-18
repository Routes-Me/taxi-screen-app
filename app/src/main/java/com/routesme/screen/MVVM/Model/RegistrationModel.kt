package com.routesme.screen.MVVM.Model

data class RegistrationCredentials(var SerialNumber: String? = null, var SimSerialNumber: String? = null, var VehicleId: String? = null)
data class RegistrationSuccessResponse(val deviceId: String? = null, val status: Boolean = false, val message: String? = null, val statusCode: Int = -999)

class RegistrationResponse(deviceId: String? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

     var deviceId: String? = null
    init {
        this.deviceId = "9"//deviceId
    }

    val isSuccess: Boolean
        get() = true//(mResponseErrors == null && mThrowable == null)
}