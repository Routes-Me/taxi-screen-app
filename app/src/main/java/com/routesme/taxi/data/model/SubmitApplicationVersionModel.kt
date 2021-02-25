package com.routesme.taxi.data.model

import com.routesme.taxi.data.model.Response
import com.routesme.taxi.data.model.ResponseErrors

data class SubmitApplicationVersionCredentials(var name: String? = null, var versions: String? = null)

class SubmitApplicationVersionResponse(val isSuccess: Boolean = false, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable)