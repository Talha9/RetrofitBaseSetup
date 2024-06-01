package com.example.retrofitbasesetup

import android.app.Dialog
import android.content.Context
import androidx.fragment.app.Fragment


abstract class BaseFragment : Fragment() {

    protected lateinit var fragmentNavhelper: FragmentNavHelper

    private var loadingDialog: Dialog? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentNavhelper = (context as FragmentNavHelper)
    }


    fun showLoading(message: String) {
        fragmentNavhelper.showLoading(message)
    }

    fun hideLoading() {
        fragmentNavhelper.hideLoading()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroy() {
        hideLoadingDialog()
        super.onDestroy()
    }

}