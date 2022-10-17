package com.vinatti.datasales.data.remote

data class ApiResult<out T>(var status: Status, val data: T?, val message: String?) {
    enum class Status {
        LOADING,
        SUCCESS,
        ERROR,
        ERROR_NETWORK
    }

    companion object {
        fun <T> success(data: T): ApiResult<T> {
            return ApiResult(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String?, data: T? = null): ApiResult<T> {
            return ApiResult(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): ApiResult<T> {
            return ApiResult(Status.LOADING, data, null)
        }
        fun <T> errorNetwork(data:T?=null): ApiResult<T> {
            return ApiResult(Status.ERROR_NETWORK,data,null)
        }

    }
}