package com.routesme.taxi.MVVM.Model

data class RegistrationCredentials(var SerialNumber: String? = null, var SimSerialNumber: String? = null, var VehicleId: String? = null)
data class RegistrationSuccessResponse(val deviceId: String? = null, val status: Boolean = false, val message: String? = null, val statusCode: Int = -999)
class RegistrationResponse(deviceId: String? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

     var deviceId: String? = null
    init {
        //this.deviceId = deviceId
        this.deviceId = "2"
    }

    val isSuccess: Boolean
        //get() = (mResponseErrors == null && mThrowable == null)
        get() = true
}