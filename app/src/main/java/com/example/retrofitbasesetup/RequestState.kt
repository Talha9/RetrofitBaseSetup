package com.example.retrofitbasesetup

sealed class RequestState {
    object Start : RequestState()
    object Loading : RequestState()
    object Success : RequestState()
    data class Error(val showErrorMsg: Boolean = false, val errorDetail: Throwable? = null) :
        RequestState()
}