package com.example.retrofitbasesetup

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder



abstract class BaseActivity : AppCompatActivity(), FragmentNavHelper {

    lateinit var alertDialog: AlertDialog
    private var loadingDialog: Dialog? = null


    private val progressBar: ProgressBar by lazy {
        ProgressBar(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    abstract override fun showInfo(code: ErrorCode, message: String)

    override fun showAlertDialog(
        title: String?,
        message: String?,
        textOk: String?,
        textCancel: String?,
        okListener: DialogInterface.OnClickListener?,
        cancelListener: DialogInterface.OnClickListener?
    ) {
        val builder = MaterialAlertDialogBuilder(this)
        title?.let { builder.setTitle(it) }
        message?.let { builder.setMessage(it) }

        if (okListener == null && cancelListener == null) {
            builder.setCancelable(true)
        } else {
            builder.setCancelable(false)
        }
        builder.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_android_black_24dp))
        if (okListener != null){
            builder.setPositiveButton(textOk) { dialog, which -> okListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE) }
        }
        if (cancelListener != null){
            builder.setNeutralButton(textCancel) { dialog, which -> cancelListener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL) }
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun showAlertDialog(
        title: String?,
        message: String?,
        textOk: String?,
        textCancel: String?,
        okListener: (() -> Unit)? = null,
        cancelListener: (() -> Unit)? = null
    ) {
        val builder = MaterialAlertDialogBuilder(this)
        title?.let { builder.setTitle(it) }
        message?.let { builder.setMessage(it) }

        if (okListener == null && cancelListener == null) {
            builder.setCancelable(true)
        } else {
            builder.setCancelable(false)
        }
        builder.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_android_black_24dp))
            .setPositiveButton(textOk) { dialog, which -> okListener?.invoke() }
            .setNeutralButton(textCancel) { dialog, which -> cancelListener?.invoke() }

        alertDialog = builder.create()
        alertDialog.show()
    }

    fun showCustomViewAlertDialog(
        title: String,
        message: String? = null,
        textOk: String? = "Save",
        textCancel: String? = "Cancel",
        @LayoutRes layoutId: Int? = null,
        view: ((view: View) -> Unit)? = null,
        okListener: ((dialog: DialogInterface) -> Unit)? = null,
        cancelListener: ((dialog: DialogInterface) -> Unit)? = null
    ) {
        val builder = MaterialAlertDialogBuilder(this)
        title.let { builder.setTitle(it) }
        message?.let { builder.setMessage(it) }

        if (okListener == null && cancelListener == null) {
            builder.setCancelable(true)
        } else {
            builder.setCancelable(false)
        }

        layoutId?.let {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val inflatedView = inflater.inflate(it, null)
            builder.setView(inflatedView)
            view?.invoke(inflatedView)
        }
        builder.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_android_black_24dp))
            .setPositiveButton(textOk) { dialog, which -> okListener?.invoke(dialog) }
            .setNegativeButton(textCancel) { dialog, which -> cancelListener?.invoke(dialog) }

        alertDialog = builder.create()
        alertDialog.show()
    }

    override fun performLogout(isAuto: Boolean) {
    }

    override fun showLoading(message: String) {
//        loadingDialog = showLoadingDialog()
        loadingDialog?.let {
            if (it.isShowing) {
                return
            }
            loadingDialog = null
        }
        loadingDialog = showLoadingDialog()
    }

    override fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    fun View.hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            this.hideSoftInputFromWindow(this@hideKeyboard.windowToken, 0)
        }
    }

    fun showKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            this.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    override fun onDestroy() {
        hideLoading()
        super.onDestroy()
    }

}