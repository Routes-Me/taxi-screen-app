package com.routesme.vehicles.data.model

data class TerminalSuccessResponse(val terminalId: String? = null, val message: String? = null, val status: Boolean = false, val statusCode: Int = -999)
class TerminalResponse(terminalId: String? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null) : Response(mResponseErrors, mThrowable) {

    var terminalId: String? = null

    init {
        this.terminalId = terminalId
    }

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}
