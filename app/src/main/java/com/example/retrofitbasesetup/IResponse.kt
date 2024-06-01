package com.example.retrofitbasesetup

sealed class IResponse<out T>(
    val isSuccessful: Boolean,
    val data: T?,
    val errorResponse: ApiError?
) {
    data class Success<out T>(val responseData: T?) : IResponse<T>(true, responseData, null)
    data class Failure<out T>(val errorData: ApiError?) : IResponse<T>(false, null, errorData)

    // NOTE:: Use this state only when we are using live data of type response from repo.
    object Loading : IResponse<Nothing>(false,null,null)
}