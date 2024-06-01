package com.example.retrofitbasesetup

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun <T : Any> runIOThenMain(work: suspend () -> IResponse<T>): IResponse<T> =
    withContext(Dispatchers.IO) {
        val resultData = work()
        withContext(Dispatchers.Main) {
            resultData
        }
    }

val <T> T.exhaustive: T
    get() = this

fun Context.showLoadingDialog(): Dialog {
    val view = LayoutInflater.from(this).inflate(R.layout.loading, null)
    val alertDialog = AlertDialog.Builder(this).setView(view).setCancelable(false)
    val dialog = alertDialog.create()
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.show()
    return dialog
}