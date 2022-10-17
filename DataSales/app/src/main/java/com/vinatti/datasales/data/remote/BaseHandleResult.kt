package com.vinatti.datasales.data.remote

import retrofit2.Call
import retrofit2.awaitResponse
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketTimeoutException

abstract class BaseHandleResult {
    protected suspend fun <T> getResult(call: suspend () -> Call<T>): ApiResult<T> {
        try {
            val response = call.invoke().awaitResponse()
            if (response.isSuccessful) {
                Timber.d("getResult: ${response.isSuccessful}")
                val body = response.body()
                if (body != null) return ApiResult.success(body)
            }
            return error(" ${response.code()} ${response.message()}")
        }catch (e: SocketTimeoutException){
            e.printStackTrace()
            return errorNetwork()
        }catch (e: FileNotFoundException){
            e.printStackTrace()
            return error(e.message ?: e.toString())
        }
        catch (e: IOException){
            e.printStackTrace()
            return errorNetwork()
        }catch (e: Exception) {
            e.printStackTrace()
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): ApiResult<T> {
        Timber.d(message)
        return ApiResult.error("Network call has failed for a following reason: $message")
    }

    private fun <T> errorNetwork(): ApiResult<T> {
        return ApiResult.errorNetwork()
    }
}