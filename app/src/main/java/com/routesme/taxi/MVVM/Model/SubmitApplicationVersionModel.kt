package com.routesme.taxi.MVVM.Model

import java.net.HttpURLConnection

data class SubmitApplicationVersionCredentials(var name: String? = null, var versions: String? = null)

data class SubmitApplicationVersionSuccessResponse(val applicationId: String? = null, val message: String? = null, val status: Boolean = false, val statusCode: Int = -999)

class SubmitApplicationVersionResponse(val submitApplicationVersionSuccessResponse: SubmitApplicationVersionSuccessResponse? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}