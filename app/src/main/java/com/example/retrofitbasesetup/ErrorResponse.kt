package com.example.retrofitbasesetup

import android.util.Log
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

const val _UNAUTHORIZED = 401
const val _REGION_LOCKED = 403
const val _NOT_FOUND = 404
const val _NO_NETWORK = 405
const val _BAD_RESPONSE = 500
const val _EMPTY_DATA = 888
const val _UNKNOWN = 5422
const val _SERVER_STRING_ERROR = 5423
const val _ACTIVE_SESSION_FOUND = 440
const val _FLOW_EXCEPTION = 5425

sealed class ErrorCode constructor(val code: Int) {
    object UNAUTHORIZED : ErrorCode(code = _UNAUTHORIZED)
    object NOT_FOUND : ErrorCode(code = _NOT_FOUND)
    object NO_NETWORK : ErrorCode(code = _NO_NETWORK)
    object REGION_LOCKED : ErrorCode(code = _REGION_LOCKED)
    object BAD_RESPONSE : ErrorCode(code = _BAD_RESPONSE)
    object EMPTY_DATA : ErrorCode(code = _EMPTY_DATA)
    object ACTIVE_SESSION_FOUND : ErrorCode(code = _ACTIVE_SESSION_FOUND)
    object SERVER_STRING_ERROR : ErrorCode(code = _SERVER_STRING_ERROR)
    object FLOW_EXCEPTION : ErrorCode(code = _FLOW_EXCEPTION)
    data class UNKNOWN(val mCode: Int = _UNKNOWN) : ErrorCode(code = mCode)

    fun isUnknown(): Boolean {
        return (code != _UNAUTHORIZED &&
                code != _NOT_FOUND &&
                code != _NO_NETWORK &&
                code != _BAD_RESPONSE &&
                code != _EMPTY_DATA)
    }
}

data class ApiError(
    val code: ErrorCode = ErrorCode.UNKNOWN(_UNKNOWN), // Note:: Default code for unknown is 5422
    override val message: String = "",
    val extra: Any? = null,
    val localizedKey: String = ""
) : Exception(message) {
    override fun toString(): String {
        return "$code : $message"
    }
}


data class AppError(
    private val code: ErrorCode = ErrorCode.UNKNOWN(_UNKNOWN), // Note:: Default code for unknown is 5422
    private val description: String = "",
    private val causeMessage: String = ""
) : Exception(causeMessage) {

    companion object {

        fun fromThrowable(throwable: Throwable): AppError {
            if (throwable is AppError) {
                return throwable
            }
            if (throwable !is DataError || throwable.description != DataError.DESC_DO_NOT_LOG)
                Log.e("AppError", Log.getStackTraceString(throwable))
            val error = when (throwable) {
                is DataError -> fromDataError(throwable)
                is ApiError -> fromApiError(throwable)
                else -> {
                    if (isInternetError(throwable)) {
                        AppError(ErrorCode.NO_NETWORK)
                    } else {
                        AppError()
                    }
                }
            }
            error.initCause(throwable)
            return error
        }

        private fun fromDataError(error: DataError): AppError {
            return AppError(error.code, causeMessage = error.description)
        }

        private fun fromApiError(error: ApiError): AppError {
            return if (error.code == ErrorCode.REGION_LOCKED) {
                geoBlockedHttp403()
            } else {
                AppError(
                    error.code,
                    description = error.message,
                    causeMessage = error.message
                )
            }
        }

        private fun geoBlockedHttp403() = AppError(
            ErrorCode.REGION_LOCKED,
            causeMessage = "HTTP Error 403 - Content might be geo blocked"
        )

        fun isInternetError(throwable: Throwable): Boolean {
            return throwable is UnknownHostException || throwable is TimeoutException
                    || throwable is SocketTimeoutException
        }
    }

    fun getCode() = code

    fun getDescription() = description

    fun getCauseMessage() = message

    override fun toString(): String {
        return "AppError(code=$code)"
    }

}

class DataError(
    val code: ErrorCode,
    val description: String = ""
) : Throwable(description) {

    companion object {

        const val ERROR_EMPTY = 0
        const val ERROR_NOT_AVAILABLE = 1

        const val DESC_DO_NOT_LOG = "doNotLog"
    }
}
