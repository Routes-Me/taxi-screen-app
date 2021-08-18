package com.routesme.vehicles.data.model

data class TerminalCredentials(var notificationIdentifier: String? = null, var deviceId: String? = null)

data class RegisterTerminalSuccessResponse(val terminalId: String? = null)

class TerminalResponse(terminalId: String? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    var terminalId: String? = null
    init {
        this.terminalId = terminalId
    }

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}