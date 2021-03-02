package com.routesme.taxi.data.model

data class RefreshTokenCredentials(var RefreshToken: String = "")

data class RefreshTokenSuccessResponse(val accessToken: String? = null, val refreshToken: String? = null, val message: String? = null, val status: Boolean = false, val statusCode: Int = -999)

class RefreshTokenResponse(accessToken: String? = null, refreshToken: String? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    var accessToken: String? = null
    var refreshToken: String? = null

    init {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}