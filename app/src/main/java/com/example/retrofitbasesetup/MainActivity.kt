package com.example.retrofitbasesetup

import android.R.attr.fragment
import android.content.DialogInterface
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.retrofitbasesetup.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportFragmentManager.beginTransaction().apply {
            replace(binding.fragParent.id, MainFragment())
            addToBackStack(null)
            commit()
        }
        binding.root
    }

    override fun showLoading(message: String) {
        super.showLoading(message)
    }


    override fun showInfo(code: ErrorCode, message: String) {

    }


    override fun handleApiError(apiError: ApiError, showErrorMsg: Boolean) {

    }

}

