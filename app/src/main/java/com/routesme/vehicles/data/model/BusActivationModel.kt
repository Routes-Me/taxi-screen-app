package com.routesme.vehicles.data.model

import com.routesme.vehicles.BuildConfig

data class BusActivationCredentials(var api_key: String = BuildConfig.NEW_API_KEY, var api_secret: String = BuildConfig.NEW_API_SECRET, var SecondID: String? = null)


//Activate bus
data class ActivateBusSuccessResponse(val status: Boolean = false, val activateBusDescription: ActivateBusDescription? = null)
data class ActivateBusDescription (val message : String?  = null, val status  : Boolean = false)
class ActivateBusResponse(activateBusDescription: ActivateBusDescription? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    var activateBusDescription: ActivateBusDescription? = null
    init {
        this.activateBusDescription = activateBusDescription
    }

    val descriptionStatus: Boolean?
        get() = activateBusDescription?.status

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}


//Deactivate bus
data class DeactivateBusSuccessResponse(val status: Boolean = false, val deactivateBusDescription: DeactivateBusDescription? = null)
data class DeactivateBusDescription (val message : String?  = null, val status  : Boolean = false)
class DeactivateBusResponse(deactivateBusDescription: DeactivateBusDescription? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    var deactivateBusDescription: DeactivateBusDescription? = null
    init {
        this.deactivateBusDescription = deactivateBusDescription
    }

    val descriptionStatus: Boolean?
        get() = deactivateBusDescription?.status

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}