package com.routesme.taxi_screen.MVVM.Model

data class Pagination(val total: Int = 0, val offset: Int = 0, val limit: Int = 0)
data class ResponseErrors(val errors: List<Error>)
data class Error(val code: Int = -999, val detail: String? = null, val status: Int = -999)

open class Response(errors: ResponseErrors?, throwable: Throwable?) {
    var responseErrors: ResponseErrors? = null
    var throwable: Throwable? = null
    init {
        this.responseErrors = errors
        this.throwable = throwable
    }
}