package com.routesme.taxi.room

data class ResponseBody<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T?): ResponseBody<T> {
            return ResponseBody(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): ResponseBody<T> {
            return ResponseBody(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): ResponseBody<T> {
            return ResponseBody(Status.LOADING, data, null)
        }

    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

}