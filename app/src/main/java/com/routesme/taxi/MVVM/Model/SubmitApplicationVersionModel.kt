package com.routesme.taxi.MVVM.Model

data class SubmitApplicationVersionCredentials(var name: String? = null, var versions: String? = null)

class SubmitApplicationVersionResponse(val isSuccess: Boolean = false, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable)