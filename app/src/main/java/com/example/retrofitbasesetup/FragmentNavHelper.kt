package com.example.retrofitbasesetup

import android.content.DialogInterface


interface FragmentNavHelper {

    fun showAlertDialog(
        title: String? = null,
        message: String? = null,
        textOk: String? = null,
        textCancel: String? = null,
        okListener: DialogInterface.OnClickListener? = null,
        cancelListener: DialogInterface.OnClickListener? = null
    )

    fun showLoading(message: String)

    fun hideLoading()

    fun showInfo(code: ErrorCode, message: String)

    fun performLogout(isAuto: Boolean = false)

    fun handleApiError(apiError: ApiError, showErrorMsg: Boolean)
}