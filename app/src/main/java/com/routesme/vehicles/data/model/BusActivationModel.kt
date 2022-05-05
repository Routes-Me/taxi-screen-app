package com.routesme.vehicles.data.model

import com.routesme.vehicles.BuildConfig

data class BusActivationCredentials(var api_key: String = BuildConfig.NEW_API_KEY, var api_secret: String = BuildConfig.NEW_API_SECRET, var SecondID: String? = null)


//Activate bus
data class ActivateBusResponseDTO(val status: Boolean = false, val description: Any? = null)
data class ActivateBusSuccessDescription (
        val id          : String?  = null,
        val active      : Boolean? = null,
        val kind        : String?  = null,
        val palteNumber : String?  = null,
        val routeID     : String?  = null,
        val routeName   : String?  = null,
        val distination : String?  = null,
        val price       : Int?     = null,
        // val driverID    : String?  = null,
        // val userName    : String?  = null,
        // val phoneNumber : String?  = null,
        // val companyID   : String?  = null,
        val company     : String?  = null,
        val socondID    : String?  = null
)
class ActivateBusResponse(val activateBusSuccessDescription: ActivateBusSuccessDescription? = null, val activateBusFailedDescription: String? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

/*
    var activateBusDescription: ActivateBusSuccessDescription? = null
    init {
        this.activateBusDescription = activateBusDescription
    }
*/

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)

    val isSuccessDescription: Boolean
        get() = (activateBusSuccessDescription != null && activateBusFailedDescription == null)
}


//Deactivate bus
data class DeactivateBusResponseDTO(val status: Boolean = false, val deactivateBusDescription: DeactivateBusDescription? = null)
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