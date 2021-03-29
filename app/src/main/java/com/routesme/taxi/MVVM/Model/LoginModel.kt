package com.routesme.taxi.MVVM.Model

import java.io.Serializable

data class Authorization(var isAuthorized: Boolean, var responseCode: Int) : Serializable
data class SignInCredentials(var userName: String = "", var password: String = "")

class UnlinkModel(val status:Boolean?=null,val message:String?=null,val statusCode:Int?=null)

data class SignInSuccessResponse(val token: String? = null,val refresh_token:String?=null, val message: String? = null, val status: Boolean = false, val statusCode: Int = -999)

class LoginResponse(token: String? = null,refresh_token:String?=null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    var token: String? = null
    var refresh_token:String?=null
    init {
        this.token = token
        this.refresh_token = refresh_token
    }

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}

data class RefreshModel(val token: String? = null,val refresh_token:String?=null, val message: String? = null, val statusCode: Int = -999)

class RefreshResponse(token: String? = null,refresh_token:String?=null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null) : Response(mResponseErrors,mThrowable){

    var token: String? = null
    var refresh_token:String?=null
    init {
        this.token = token
        this.refresh_token = refresh_token
    }

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)


}

class UnlinkResponse(val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}