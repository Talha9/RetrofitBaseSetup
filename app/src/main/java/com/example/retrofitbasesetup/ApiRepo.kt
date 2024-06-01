package com.example.retrofitbasesetup

import android.util.Log
import android.util.MalformedJsonException
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException

interface ApiRepo {
    suspend fun <T> handleRequest(call: suspend () -> Response<T>): IResponse<T> {
        return try {
            val apApiResponse = call.invoke()
            if (apApiResponse.isSuccessful) {
                IResponse.Success(apApiResponse.body())
            } else {
                handleFailureResponse(
                    apApiResponse.code(),
                    apApiResponse.message(),
                    apApiResponse.errorBody()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            handleException(e)
        }
    }

    private fun <T> handleFailureResponse(
        code: Int,
        message: String,
        errorBody: ResponseBody?
    ): IResponse<T> {

        try {
            val errorCode: ErrorCode = when (code) {
                401,440 -> ErrorCode.UNAUTHORIZED
                404 -> ErrorCode.NOT_FOUND
                500 -> ErrorCode.BAD_RESPONSE
                405 -> ErrorCode.NO_NETWORK
                else -> ErrorCode.UNKNOWN(code)
            }
            return IResponse.Failure(ApiError(errorCode, "$message"))
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(
                "BaseRepository",
                e.message ?: "Unknown error while handling failure response"
            )
        }

        return IResponse.Failure(ApiError(ErrorCode.UNKNOWN()))
    }

    private fun <T> handleException(exception: Exception?): IResponse<T> {
        exception?.let {
            val apiError = when (exception) {
                is ConnectException -> {
                    ApiError(ErrorCode.NO_NETWORK, message = "No internet Connection") //
                }
                is MalformedJsonException -> {
                    ApiError(ErrorCode.BAD_RESPONSE, message = exception.message.toString())
                }
                is UnknownHostException -> {
                    ApiError(ErrorCode.NOT_FOUND, message = exception.message.toString())
                }
                else -> {
                    ApiError(ErrorCode.UNKNOWN(), message = exception.message.toString())
                }
            }
            return IResponse.Failure(apiError)
        }
        return IResponse.Failure(
            ApiError(ErrorCode.UNKNOWN(), message = exception?.message.toString())
        )
    }

    // use this method when you want response of type LiveData
    fun <T> resultLiveData(
        scope: CoroutineScope,
        call: suspend () -> IResponse<T>
    ): LiveData<IResponse<T>> {
        return liveData(scope.coroutineContext) {
            emit(IResponse.Loading)
            withContext(Dispatchers.IO) {
                emit(call.invoke())
            }
          }
       }
}