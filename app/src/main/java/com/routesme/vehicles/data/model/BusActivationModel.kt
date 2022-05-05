package com.routesme.vehicles.data.model

import com.routesme.vehicles.BuildConfig

data class ActivateBusCredentials(var api_key: String = BuildConfig.NEW_API_KEY, var api_secret: String = BuildConfig.NEW_API_SECRET, var SecondID: String? = null)
data class ActivateBusSuccessResponse(val status: Boolean = false, val description: Description? = null)
data class Description (val message : String?  = null, val status  : Boolean = false)
class ActivateBusResponse(description: Description? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    var description: Description? = null
    init {
        this.description = description
    }

    val descriptionStatus: Boolean?
        get() = description?.status

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}