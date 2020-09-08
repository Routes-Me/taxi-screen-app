package com.routesme.taxi_screen.kotlin.MVVM.Model

import com.routesme.taxi_screen.kotlin.Model.Response
import com.routesme.taxi_screen.kotlin.Model.ResponseErrors

data class SignInCredentials(var Username: String = "", var Password: String = "")

data class SignInSuccessResponse(val token: String? = null, val message: String? = null, val status: Boolean = false, val responseCode: Int = -999)

class LoginResponse(token: String? = null, errors: ResponseErrors? = null, throwable: Throwable? = null): Response(errors, throwable) {
    var isSuccess = false
    var token: String? = null

    init {
        this.token = token
        this.isSuccess = !token.isNullOrEmpty()
    }
}