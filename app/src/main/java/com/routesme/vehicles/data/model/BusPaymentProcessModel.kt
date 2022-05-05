package com.routesme.vehicles.data.model

import com.routesme.vehicles.BuildConfig

data class BusPaymentProcessCredentials(var api_key: String = BuildConfig.NEW_API_KEY, var api_secret: String = BuildConfig.NEW_API_SECRET, var SecondID: String? = null, var Value: Int? = null, var UserID: String? = null)

data class BusPaymentProcessDTO(val status: Boolean = false, val description: Any? = null)
data class BusPaymentProcessSuccessDTO (val message: String?  = null, val status: Boolean = false)

class BusPaymentProcessResponse(val isProcessedSuccessfully: Boolean = false, val busPaymentProcessSuccessDTO: BusPaymentProcessSuccessDTO? = null, val busPaymentProcessFailedDTO: String? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)

}