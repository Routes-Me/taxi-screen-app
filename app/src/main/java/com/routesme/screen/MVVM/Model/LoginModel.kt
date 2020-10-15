package com.routesme.screen.MVVM.Model

import java.io.Serializable

data class Authorization(var isAuthorized: Boolean, var responseCode: Int) : Serializable
data class SignInCredentials(var userName: String = "", var password: String = "")

data class SignInSuccessResponse(val token: String? = null, val message: String? = null, val status: Boolean = false, val statusCode: Int = -999)

class LoginResponse(token: String? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    var token: String? = null
    init {
        this.token = token
    }

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}